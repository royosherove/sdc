package org.openecomp.sdc.vendorsoftwareproduct.impl.orchestration;

import org.apache.commons.collections4.MapUtils;
import org.openecomp.core.utilities.file.FileContentHandler;
import org.openecomp.core.utilities.file.FileUtils;
import org.openecomp.core.utilities.orchestration.OnboardingTypesEnum;
import org.openecomp.sdc.common.errors.Messages;
import org.openecomp.sdc.common.utils.SdcCommon;
import org.openecomp.sdc.datatypes.error.ErrorLevel;
import org.openecomp.sdc.datatypes.error.ErrorMessage;
import org.openecomp.sdc.logging.api.Logger;
import org.openecomp.sdc.logging.api.LoggerFactory;
import org.openecomp.sdc.vendorsoftwareproduct.dao.type.VspDetails;
import org.openecomp.sdc.vendorsoftwareproduct.services.filedatastructuremodule.CandidateService;
import org.openecomp.sdc.vendorsoftwareproduct.types.UploadFileResponse;

import java.io.InputStream;
import java.util.Optional;

import static org.openecomp.core.validation.errors.ErrorMessagesFormatBuilder.getErrorWithParameters;

public abstract class BaseOrchestrationTemplateHandler implements OrchestrationTemplateFileHandler {
  protected static final Logger logger =
      LoggerFactory.getLogger(BaseOrchestrationTemplateHandler.class);
  @Override
  public UploadFileResponse upload(VspDetails vspDetails, InputStream fileToUpload,
                                   String fileSuffix, String networkPackageName,
                                   CandidateService candidateService) {
    UploadFileResponse uploadFileResponse = new UploadFileResponse();
    uploadFileResponse.setOnboardingType(getHandlerType());
    if (isNotEmptyFileToUpload(fileSuffix, fileToUpload, uploadFileResponse, candidateService)) {
      return uploadFileResponse;
    }

    byte[] uploadedFileData = FileUtils.toByteArray(fileToUpload);
    if (isInvalidRawZipData(fileSuffix, uploadFileResponse, uploadedFileData, candidateService)) {
      return uploadFileResponse;
    }

    Optional<FileContentHandler> optionalContentMap =
        getFileContentMap(uploadFileResponse, uploadedFileData);
    if (!optionalContentMap.isPresent()) {
      logger.error(getErrorWithParameters(Messages.FILE_CONTENT_MAP.getErrorMessage(),
          getHandlerType().toString()));
      uploadFileResponse.addStructureError(SdcCommon.UPLOAD_FILE, new ErrorMessage(ErrorLevel.ERROR,
          getErrorWithParameters(Messages.FILE_CONTENT_MAP.getErrorMessage(),
              getHandlerType().toString())));
      return uploadFileResponse;
    }

    if (!MapUtils.isEmpty(uploadFileResponse.getErrors())) {
      return uploadFileResponse;
    }
    if (updateCandidateData(vspDetails, uploadedFileData, optionalContentMap.get(), fileSuffix,
        networkPackageName, candidateService, uploadFileResponse)) {
      return uploadFileResponse;
    }
    return uploadFileResponse;

  }

  protected abstract boolean updateCandidateData(VspDetails vspDetails,
                                                 byte[] uploadedFileData,
                                                 FileContentHandler contentMap,
                                                 String fileSuffix,
                                                 String networkPackageName,
                                                 CandidateService candidateService,
                                                 UploadFileResponse uploadFileResponse);

  private boolean isNotEmptyFileToUpload(String fileSuffix, InputStream fileToUpload,
                                         UploadFileResponse uploadFileResponse,
                                         CandidateService candidateService) {
    Optional<ErrorMessage> errorMessage =
        candidateService.validateNonEmptyFileToUpload(fileToUpload, fileSuffix);
    if (errorMessage.isPresent()) {
      uploadFileResponse.addStructureError(SdcCommon.UPLOAD_FILE, errorMessage.get());
      return true;
    }
    return false;
  }

  protected boolean isInvalidRawZipData(String fileSuffix,
                                        UploadFileResponse uploadFileResponse,
                                        byte[] uploadedFileData,
                                        CandidateService candidateService) {
    Optional<ErrorMessage> errorMessage;
    errorMessage = candidateService.validateRawZipData(fileSuffix, uploadedFileData);
    if (errorMessage.isPresent()) {
      uploadFileResponse.addStructureError(SdcCommon.UPLOAD_FILE, errorMessage.get());
      return true;
    }
    return false;
  }

  public abstract Optional<FileContentHandler> getFileContentMap(
      UploadFileResponse uploadFileResponse,
      byte[] uploadedFileData);

  protected abstract OnboardingTypesEnum getHandlerType();
}
