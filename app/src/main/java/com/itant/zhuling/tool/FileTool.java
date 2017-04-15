package com.itant.zhuling.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 文件相关工具方法
 * @author iTant
 *
 */
public class FileTool {

	/**
	 * 初始化目录
	 * @param dir
	 */
	public static void initDirectory(String dir) {
		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
	}

	/**
	 * 在应用的file文件夹新建一个文件夹，并将该文件夹的路径返回
	 * 
	 * @param context 上下文
	 * @param forlderName 文件夹的名字
	 * @return 新建的文件夹路径
	 */
	public String makeFilePath(Context context, String forlderName) {
		if (TextUtils.isEmpty(forlderName)) {
			return null;
		}
		File cacheDir = new File(getFileDir(context), forlderName);
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		return cacheDir.getPath();
	}

	/**
	 * 获取文件目录
	 * 
	 * @param context 上下文
	 * @return 如果有SD卡的话，返回SD卡根目录；如果没有SD卡，则返回应用file文件夹
	 */
	public File getFileDir(Context context) {
		File cacheDir = null;

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			cacheDir = context.getExternalFilesDir(null);
		} else {
			cacheDir = context.getFilesDir();

		}
		return cacheDir;
	}

	/**
	 * 创建文件目录
	 * 
	 * @param dir 完整的文件夹路径
	 * @return 代表文件夹的File对象
	 */
	public File createFileDir(String dir) {
		if (TextUtils.isEmpty(dir)) {
			return null;
		}

		File cacheDir = new File(dir);
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		return cacheDir;
	}
	
	/**
	 * 拷贝文件到指定目录
	 * 
	 * @param fromFile 源文件完整路径
	 * @param toFile 目标文件完整路径
	 */
	public void copyFile(String fromFile, String toFile) {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			inputStream = new FileInputStream(fromFile);
			outputStream = new FileOutputStream(toFile);
			byte[] b = new byte[1024];
			int readedLength = -1;
			while ((readedLength = inputStream.read(b)) != -1) {
				outputStream.write(b, 0, readedLength);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 保存图片为jpeg格式的图片
	 * 
	 * @param fullFileName 图片保存的路径（包括文件名和后缀名）
	 * @param mBitmap 图片位图信息
	 */
	public static void saveBitmapToFile(Bitmap mBitmap, String fullFileName) {
		if (TextUtils.isEmpty(fullFileName)) {
			return;
		}

		File f = new File(fullFileName);
		try {
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
		} catch (Exception e) {
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fOut != null) {
					fOut.close();
				}
			} catch (IOException e2) {
			}
		}

	}
	
	/**
	 * 返回某一个文件夹的目录
	 */
	public File getLocalPacketsDirectory(String packageName) {
		return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + packageName);
	}
	
	/**
	 * 搜索某文件夹下面所有的报文(可能有子文件夹)
	 * 
	 * @param rootFile 根目录
	 * @param suffix 后缀（如".exe"）
	 */
	public void searchePacket(File rootFile, String suffix, List<File> fileList) {
		File[] files = rootFile.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					String fileName = file.getName();
					if (fileName.endsWith(suffix)) {
						// 如果是报文文件的话
						fileList.add(file);
					}
				} else if (file.isDirectory()) {
					searchePacket(file, suffix, fileList);
				}
			}
		}
	}
	
	/**
	 * 删除某文件夹下面所有的文件
	 * 
	 * @param rootFile 根目录
	 */
	public static boolean deletePacketsByDirectory(File rootFile) {
		File[] files = rootFile.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					// 是文件，直接删除
					if (!file.delete()) {
						return false;
					}
				} else if (file.isDirectory()) {
					// 是目录，继续递归删除
					if (!deletePacketsByDirectory(file)) {
						return false;
					}
				}
			}
		}
		
		return rootFile.delete();
	}
	
	/**
	 * 计算缓存大小
	 * @param targetFile 目标文件夹
	 * @return 本应用的磁盘缓存大小（单位：bit）
	 */
	public static long calculateCacheSize(File targetFile) {
		long size = 0;
		File[] files = targetFile.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					size += calculateCacheSize(file);
				} else {
					size += file.length();
				}
			}
		}
		return size;
	}
	
	/**
	 * 删除文件夹下的所有文件（仅删除文件及子文件夹的文件，不删除文件夹和子文件夹）
	 * 
	 * @param targetFile 代表目标文件夹的File
	 * 
	 */
	public void deleteFiles(File targetFile) {
		File[] files = targetFile.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					deleteFiles(file);
				} else {
					file.delete();
				}
			}
		}
	}
	
	/**
     * 将要备份的文本存放在文件里
     * 
     * @param context 上下文
     * @param fileName 保存信息的文件的文件名
     * @param info 要备份的信息
     */
    public void saveAllInfos(Context context, String fileName, String info) {
        if (info != null) {
            try {
                FileOutputStream outStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                outStream.write(info.getBytes());
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * @return SD卡剩余容量（单位：MB）
     */
    public long getAvailableSDSize(){
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory(); 
        StatFs sf = new StatFs(path.getPath()); 
        //获取单个数据块的大小(bit)
        long blockSize = sf.getBlockSize(); 
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        //返回SD卡空闲大小(MB)
        return (freeBlocks * blockSize) >> 20;
    }

    /**
     * 获取SD卡总容量（单位：bit）
     */
    public long getSDSize(){
        File path = Environment.getExternalStorageDirectory(); 
        StatFs stat = new StatFs(path.getPath()); 
        long blockSize = stat.getBlockSize(); 
        long availableBlocks = stat.getBlockCount();
        return availableBlocks * blockSize; 
    }

    /**
     * 获取剩余磁盘空间（单位：MB）
     * @return
     */
    public long getAvailableDiskSize() {
        File root = Environment.getRootDirectory();  
        StatFs sf = new StatFs(root.getPath());  
        long blockSize = sf.getBlockSize();  
        long availCount = sf.getAvailableBlocks();  
        return (availCount * blockSize) >> 20;
    }
}