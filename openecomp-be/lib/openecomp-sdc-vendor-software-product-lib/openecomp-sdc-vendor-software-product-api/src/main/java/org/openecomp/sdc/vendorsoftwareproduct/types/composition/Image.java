package org.openecomp.sdc.vendorsoftwareproduct.types.composition;


public class Image implements CompositionDataEntity {

  private String fileName;
  private String description;
  /*private String version;
  private String format;
  private String md5;
  //private String providedBy;*/

  public Image(){
  }

  public Image(String fileName){
    this.fileName=fileName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /*public String getMd5() {
    return md5;
  }

  public void setMd5(String md5) {
    this.md5 = md5;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }*/

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /*public String getProvidedBy() {
    return providedBy;
  }

  public void setProvidedBy(String providedBy) {
    this.providedBy = providedBy;
  }*/

}
