/*
 * Copyright © 2018 European Support Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on a "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openecomp.sdc.onboarding;

import static org.openecomp.sdc.onboarding.Constants.CHECKSUM;
import static org.openecomp.sdc.onboarding.Constants.COLON;
import static org.openecomp.sdc.onboarding.Constants.DOT;
import static org.openecomp.sdc.onboarding.Constants.JAR;
import static org.openecomp.sdc.onboarding.Constants.JAVA_EXT;
import static org.openecomp.sdc.onboarding.Constants.SHA1;
import static org.openecomp.sdc.onboarding.Constants.UNICORN;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

class BuildHelper {


    private static final Map<String, String> STORE = new HashMap<>();
    private static final Logger LOG = Logger.getAnonymousLogger();

    private BuildHelper() {
        // do not remove.
    }

    static String getSnapshotSignature(File snapshotFile, String moduleCoordinate, String version) {
        String key = moduleCoordinate + ":" + version;
        String signature = STORE.get(key);
        if (signature != null) {
            return signature;
        }
        try {
            signature = new String(fetchSnapshotSignature(snapshotFile, version));
            if (version.equals(signature)) {
                signature = getSha1For(snapshotFile, Paths.get(snapshotFile.getParentFile().getAbsolutePath(),
                        moduleCoordinate.substring(moduleCoordinate.indexOf(':') + 1) + "-" + version + DOT + JAR + DOT
                                + SHA1).toFile());
            }
            STORE.put(key, signature);
            return signature;
        } catch (IOException | NoSuchAlgorithmException e) {
            LOG.log(Level.FINE, e.getMessage(), e);
            return version;
        }

    }

    private static String getSha1For(File file, File signatureFile) throws IOException, NoSuchAlgorithmException {
        if (signatureFile.exists()) {
            return new String(Files.readAllBytes(signatureFile.toPath()));
        }
        return getSourceChecksum(Files.readAllBytes(file.toPath()), SHA1);
    }

    static long getChecksum(File file, String fileType) {
        try {
            return readSources(file, fileType).hashCode();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static String getSourceChecksum(String data, String hashType) throws NoSuchAlgorithmException {
        return getSourceChecksum(data.getBytes(), hashType);
    }

    static String getSourceChecksum(byte[] data, String hashType) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(hashType);
        md.update(data);
        byte[] hashBytes = md.digest();

        StringBuilder buffer = new StringBuilder();
        for (byte hashByte : hashBytes) {
            buffer.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
        }
        return buffer.toString();
    }


    private static Map<String, List<String>> readSources(File file, String fileType) throws IOException {
        Map<String, List<String>> source = new HashMap<>();

        if (!file.exists()) {
            return source;
        }

        try (Stream<Path> pathStream = Files.walk(Paths.get(file.getAbsolutePath()))) {
            File[] selectedFiles = pathStream.filter(
                    JAVA_EXT.equals(fileType) ? BuildHelper::isRegularJavaFile : Files::isRegularFile)
                                           .map(Path::toFile).toArray(File[]::new);
            source.putAll(ForkJoinPool.commonPool()
                                  .invoke(new FileReadTask(selectedFiles, file.getAbsolutePath())));
            return source;
        }
    }

    private static boolean isRegularJavaFile(Path path) {
        File file = path.toFile();
        return file.isFile() && file.getName().endsWith(JAVA_EXT);
    }

    private static class FileReadTask extends RecursiveTask<Map<String, List<String>>> {

        private final Map<String, List<String>> store = new HashMap<>();
        final File[] files;
        final String pathPrefix;
        private static final int MAX_FILES = 10;

        FileReadTask(File[] files, String pathPrefix) {
            this.files = files;
            this.pathPrefix = pathPrefix;
        }

        private static List<String> getData(File file) throws IOException {
            List<String> coll = Files.readAllLines(file.toPath(), StandardCharsets.ISO_8859_1);
            if (file.getAbsolutePath().contains(File.separator + "generated-sources" + File.separator)) {
                coll.removeIf(s -> s == null || s.trim().startsWith("/") || s.trim().startsWith("*"));
            }
            return coll;
        }


        @Override
        protected Map<String, List<String>> compute() {
            if (files.length > MAX_FILES) {
                FileReadTask task1 = new FileReadTask(Arrays.copyOfRange(files, 0, files.length / 2), pathPrefix);
                FileReadTask task2 =
                        new FileReadTask(Arrays.copyOfRange(files, files.length / 2, files.length), pathPrefix);
                task1.fork();
                task2.fork();
                store.putAll(task1.join());
                store.putAll(task2.join());
            } else {
                for (File toRead : files) {
                    try {
                        store.put(toRead.getAbsolutePath().substring(pathPrefix.length())
                                        .replace(File.separatorChar, '.'), getData(toRead));
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            }

            return store;
        }
    }

    static Optional<String> getArtifactPathInLocalRepo(String repoPath, MavenProject project, byte[] sourceChecksum)
            throws MojoFailureException {
        STORE.put(project.getGroupId() + COLON + project.getArtifactId() + COLON + project.getVersion(),
                new String(sourceChecksum));
        URI uri;
        try {
            uri = new URI(repoPath + (project.getGroupId().replace('.', '/')) + '/' + project.getArtifactId() + '/'
                                  + project.getVersion());
        } catch (URISyntaxException e) {
            throw new MojoFailureException(e.getMessage(), e);
        }
        File f = new File(uri);
        File[] list = f.listFiles(t -> t.getName().equals(project.getArtifactId() + "-" + project.getVersion() + "."
                                                                  + project.getPackaging()));
        if (list != null && list.length > 0) {
            try {
                if (Arrays.equals(sourceChecksum, fetchSnapshotSignature(list[0], project.getVersion()))) {
                    return Optional.of(list[0].getAbsolutePath());
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return Optional.empty();
    }

    private static byte[] fetchSnapshotSignature(File file, String version) throws IOException {

        byte[] data = Files.readAllBytes(file.toPath());
        try (ByteArrayInputStream byteInputStream = new ByteArrayInputStream(data);
                JarInputStream jarInputStream = new JarInputStream(byteInputStream)) {

            JarEntry entry;
            while ((entry = jarInputStream.getNextJarEntry()) != null) {
                if (entry.getName().endsWith(UNICORN + DOT + CHECKSUM)) {
                    byte[] sigStore = new byte[1024];
                    return new String(sigStore, 0, jarInputStream.read(sigStore, 0, 1024)).getBytes();
                }
            }
        }
        return version.getBytes();
    }

    static <T> Optional<T> readState(String fileName, Class<T> clazz) {

        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
                ObjectInputStream ois = new ObjectInputStream(is)) {
            return Optional.of(clazz.cast(ois.readObject()));
        } catch (Exception e) {
            LOG.log(Level.FINE, e.getMessage(), e);
            return Optional.empty();
        }
    }

}
