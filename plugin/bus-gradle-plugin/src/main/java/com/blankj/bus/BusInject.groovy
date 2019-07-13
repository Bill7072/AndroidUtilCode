package com.blankj.bus

import com.blankj.bus.util.ZipUtils
import org.apache.commons.io.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

class BusInject {

    static void start(Map<String, BusInfo> busMap, File apiJar) {
        String jarPath = apiJar.getAbsolutePath()
        String decompressedJarPath = jarPath.substring(0, jarPath.length() - 4);
        File decompressedJar = new File(decompressedJarPath)
        ZipUtils.unzipFile(apiJar, decompressedJar)

        File apiUtilsFile = new File(decompressedJarPath + Config.FILE_SEP + Config.BUS_UTILS_CLASS)

        ClassReader cr = new ClassReader(apiUtilsFile.bytes);
        ClassWriter cw = new ClassWriter(cr, 0);
        ClassVisitor cv = new BusUtilsClassVisitor(cw, busMap);
        cr.accept(cv, ClassReader.SKIP_FRAMES);

        FileUtils.writeByteArrayToFile(apiUtilsFile, cw.toByteArray())

        FileUtils.forceDelete(apiJar)
        ZipUtils.zipFiles(Arrays.asList(decompressedJar.listFiles()), apiJar)
        FileUtils.forceDelete(decompressedJar)
    }
}