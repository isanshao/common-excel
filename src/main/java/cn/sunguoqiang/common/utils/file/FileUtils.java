package cn.sunguoqiang.common.utils.file;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件处理工具类
 *
 * @author sgq
 */
public class FileUtils {
    public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";

    /**
     * 输出指定文件的byte数组
     *
     * @param filePath 文件路径
     * @param os       输出流
     * @return
     */
    public static void writeBytes(String filePath, OutputStream os) throws IOException {
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException(filePath);
            }
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int length;
            while ((length = fis.read(b)) > 0) {
                os.write(b, 0, length);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            IOUtils.close(os);
            IOUtils.close(fis);
        }
    }


    /**
     * 写数据到文件中
     *
     * @param data      数据
     * @param filePath 目标文件
     * @return 目标文件
     * @throws IOException IO异常
     */
    public static void writeBytes(byte[] data, String filePath) throws IOException {
        FileOutputStream fos = null;
        try {
            String extension = getFileExtendName(data);
            fos = new FileOutputStream(new File(filePath));
            fos.write(data);
        } finally {
            IOUtils.close(fos);
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件
     * @return
     */
    public static boolean deleteFile(String filePath) {
        boolean flag = false;
        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            flag = file.delete();
        }
        return flag;
    }

    /**
     * 文件名称验证
     *
     * @param filename 文件名称
     * @return true 正常 false 非法
     */
    public static boolean isValidFilename(String filename) {
        return filename.matches(FILENAME_PATTERN);
    }

    /**
     * 下载文件名重新编码
     *
     * @param request  请求对象
     * @param fileName 文件名
     * @return 编码后的文件名
     */
    public static String setFileDownloadHeader(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
        final String agent = request.getHeader("USER-AGENT");
        String filename = fileName;
        if (agent.contains("MSIE")) {
            // IE浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        } else if (agent.contains("Firefox")) {
            // 火狐浏览器
            filename = new String(fileName.getBytes(), "ISO8859-1");
        } else if (agent.contains("Chrome")) {
            // google浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        } else {
            // 其它浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }

    /**
     * 下载文件名重新编码
     *
     * @param response     响应对象
     * @param realFileName 真实文件名
     */
    public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) throws UnsupportedEncodingException {
        String percentEncodedFileName = percentEncode(realFileName);

        StringBuilder contentDispositionValue = new StringBuilder();
        contentDispositionValue.append("attachment; filename=")
                .append(percentEncodedFileName)
                .append(";")
                .append("filename*=")
                .append("utf-8''")
                .append(percentEncodedFileName);

        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
        response.setHeader("Content-disposition", contentDispositionValue.toString());
        response.setHeader("download-filename", percentEncodedFileName);
    }

    /**
     * 百分号编码工具方法
     *
     * @param s 需要百分号编码的字符串
     * @return 百分号编码后的字符串
     */
    public static String percentEncode(String s) throws UnsupportedEncodingException {
        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        return encode.replaceAll("\\+", "%20");
    }

    /**
     * 获取图像后缀
     *
     * @param photoByte 图像数据
     * @return 后缀名
     */
    public static String getFileExtendName(byte[] photoByte) {
        String strFileExtendName = "jpg";
        if ((photoByte[0] == 71) && (photoByte[1] == 73) && (photoByte[2] == 70) && (photoByte[3] == 56)
                && ((photoByte[4] == 55) || (photoByte[4] == 57)) && (photoByte[5] == 97)) {
            strFileExtendName = "gif";
        } else if ((photoByte[6] == 74) && (photoByte[7] == 70) && (photoByte[8] == 73) && (photoByte[9] == 70)) {
            strFileExtendName = "jpg";
        } else if ((photoByte[0] == 66) && (photoByte[1] == 77)) {
            strFileExtendName = "bmp";
        } else if ((photoByte[1] == 80) && (photoByte[2] == 78) && (photoByte[3] == 71)) {
            strFileExtendName = "png";
        }
        return strFileExtendName;
    }

    /**
     * 获取文件名称 /profile/upload/2022/04/16/dzms.png -- dzms.png
     *
     * @param fileName 路径名称
     * @return 没有文件路径的名称
     */
    public static String getName(String fileName) {
        if (fileName == null) {
            return null;
        }
        int lastUnixPos = fileName.lastIndexOf('/');
        int lastWindowsPos = fileName.lastIndexOf('\\');
        int index = Math.max(lastUnixPos, lastWindowsPos);
        return fileName.substring(index + 1);
    }

    /**
     * 获取不带后缀文件名称 /profile/upload/2022/04/16/dzms.png -- dzms
     *
     * @param fileName 路径名称
     * @return 没有文件路径和后缀的名称
     */
    public static String getNameNotSuffix(String fileName) {
        if (fileName == null) {
            return null;
        }
        String baseName = FilenameUtils.getBaseName(fileName);
        return baseName;
    }

    /**
     * 删除目录、或文件。注意：如果失败，有可能已经删除了一部分文件，只有全部删除成功才返回true
     * <pre>
     * 1、为文件时，删除；
     * 2、为目录时，suffis为null,删除所有文件；
     * 3、suffis不为null时，filterSuffix为true,只删除匹配后缀的文件;filterSuffix为false,只删除不匹配后缀的文件
     * </pre>
     *
     * @param folderOrFile File 目录或文件
     * @param suffix       String 为目录时，uffis不为null时，只删除匹配后缀的文件；suffis为null,删除所有文件
     * @param filterSuffix suffix不为空时，有效。filterSuffix为true,只删除匹配后缀的文件;filterSuffix为false,只删除不匹配后缀的文件
     * @return boolean 全部删除成功，返回true,否则返回false
     */
    public static boolean deleteFolderOrFile(File folderOrFile, String suffix,
                                             boolean filterSuffix) {
        File[] allFiles = null;
        int len = 0;
        if (folderOrFile == null) {
            return false;
        }
        if (!folderOrFile.exists()) { //不存在
            return false;
        }
        if (folderOrFile.isFile()) { //为文件
            return folderOrFile.delete();
        }
        //为目录
        try {
            boolean hasDeleted = true; //目录是否已删除
            //得到该文件夹下的所有文件夹和文件数组
            allFiles = listFiles(folderOrFile, suffix, filterSuffix);
            if (allFiles == null || allFiles.length == 0) {
                hasDeleted = true; //为空，可以删除
            } else {
                len = allFiles.length;
            }
            for (int i = 0; i < len; i++) {
                if (hasDeleted) { //为true时操作
                    if (allFiles[i].isDirectory()) {
                        hasDeleted = deleteFolderOrFile(allFiles[i], suffix,
                                filterSuffix); //如果为文件夹,则递归调用删除文件夹的方法
                    } else if (allFiles[i].isFile()) {
                        try { //删除文件
                            if (!allFiles[i].delete()) {
                                hasDeleted = false; //删除失败,返回false
                            }
                        } catch (Exception se) {
                            se.printStackTrace();
                            hasDeleted = false; //异常,返回false
                        }
                    }
                } else {
                    break; //为false,跳出循环
                }
            }
            if (hasDeleted) {
                folderOrFile.delete(); //该文件夹已为空文件夹,删除它
            }
            return hasDeleted;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 返回表示此抽象路径名所表示目录中的文件和目录的抽象路径名数组.
     * 1、suffis为null,返回所有文件；
     * 2、suffis不为null时，filterSuffix为true,只返回与后缀匹配后缀的文件;filterSuffix为false,只返回与后缀不匹配后缀的文件
     *
     * @param file         查找的路径
     * @param suffix       String 后缀,不区分大小写，如 "jsp",可以为null
     * @param filterSuffix boolean suffix不为null时，有效。filterSuffix为true,只返回与后缀匹配后缀的文件;filterSuffix为false,只返回与后缀不匹配后缀的文件
     * @return File[] 此抽象路径名所表示目录中的文件和目录的抽象路径名数组
     */
    public static File[] listFiles(File file, String suffix,
                                   boolean filterSuffix) {
        if (file == null) {
            return null;
        }
        if (suffix == null) {
            return file.listFiles(); //返回一个抽象路径名数组，这些路径名表示此抽象路径名所表示目录中的文件。
        } else {
            return file.listFiles(new SuffixFilter(suffix, filterSuffix));
        }
    }

    /**
     * FilenameFilter
     */
    private static class SuffixFilter implements FilenameFilter {
        private String suffix = "";
        private boolean filterSuffix = false;

        SuffixFilter(String suffix, boolean filterSuffix) {
            this.suffix = suffix;
            this.filterSuffix = filterSuffix;
        }

        public boolean accept(File dir, String name) {
            File file = new File(dir, name);
            int index = name.lastIndexOf(".");
            String suf = name.substring(index + 1);
            if (file.isDirectory()) { //跳过目录
                return true;
            } else if (index != -1 && suf.equalsIgnoreCase(suffix)) { //包含过滤的文件(index!=-1因为有些文件没有后缀)
                return filterSuffix;
            }
            return !filterSuffix;
        }
    }
}
