package com.spshop.stylistpark.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/** 
 * from https://github.com/AWCNTT/ArticleTranslateProject/blob/master/sources/Issue%2322/Unzipping%20Files%20with%20Android%20(Programmatically).md
 * @author jon 
 */ 
public class Decompress { 
  private String _zipFile;
  private String _location;
  private String _newFileName;

  public Decompress(String zipFile, String location) { 
    _zipFile = zipFile; 
    _location = location; 

    _dirChecker(""); 
  }
  
  public Decompress(String zipFile, String location, String newFileName) { 
      _zipFile = zipFile; 
      _location = location;
      _newFileName = newFileName;

      _dirChecker(""); 
    } 

  public void unzip() { 
    try  { 
      FileInputStream fin = new FileInputStream(_zipFile); 
      ZipInputStream zin = new ZipInputStream(fin); 
      ZipEntry ze = null; 
      while ((ze = zin.getNextEntry()) != null) { 
        Log.v("Decompress", "Unzipping " + ze.getName()); 

        if(ze.isDirectory()) { 
          _dirChecker(ze.getName()); 
        } else { 
          String fileName = TextUtils.isEmpty(_newFileName)?ze.getName():_newFileName;
          if(!_location.endsWith("/")) {
              _location += "/";
          }
          Log.d("Decompress", "unzip output: " + _location + fileName);
          FileOutputStream fout = new FileOutputStream(_location + fileName); 
          for (int c = zin.read(); c != -1; c = zin.read()) { 
            fout.write(c); 
          } 

          zin.closeEntry(); 
          fout.close(); 
        } 

      } 
      zin.close(); 
      Log.v("Decompress", "unzip finish"); 
    } catch(Exception e) { 
      Log.e("Decompress", "unzip error ", e); 
    } 

  } 

  private void _dirChecker(String dir) { 
    File f = new File(_location + dir); 

    if(!f.isDirectory()) { 
      f.mkdirs(); 
    } 
  } 
} 