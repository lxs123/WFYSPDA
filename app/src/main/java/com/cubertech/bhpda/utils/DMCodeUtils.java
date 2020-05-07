package com.cubertech.bhpda.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class DMCodeUtils {
    /**
     * 创建DM二维码位图
     *
     * @param content 字符串内容(支持中文)
     * @param width   位图宽度(单位:px)
     * @param height  位图高度(单位:px)
     * @return
     */
    @Nullable
    public static Bitmap createDMCodeBitmap(String content, int width, int height) {
        return createDMCodeBitmap(content, width, height, "UTF-8", "H",
                "2", Color.BLACK, Color.WHITE, true, BarcodeFormat.DATA_MATRIX, false);
    }

    /**
     * 创建 条形码
     *
     * @param content
     * @param width
     * @param height
     * @return
     */
    @Nullable
    public static Bitmap createBarCodeBitmap(String content, int width, int height) {
        return createDMCodeBitmap(content, width, height, "UTF-8", "H",
                "2", Color.BLACK, Color.WHITE, false, BarcodeFormat.CODE_128, true);
    }

    /**
     * 创建 条形码，可以控制 条形码下方文字显示
     *
     * @param content
     * @param width
     * @param height
     * @param isShowBarCodeContent 是否让文字显示
     * @return
     */
    @Nullable
    public static Bitmap createBarCodeBitmap(String content, int width, int height, boolean isShowBarCodeContent) {
        return createDMCodeBitmap(content, width, height, "UTF-8", "H",
                "2", Color.BLACK, Color.WHITE, false, BarcodeFormat.CODE_128, isShowBarCodeContent);
    }

    /**
     * 创建 二维码 （普通类型 QR码等等）
     *
     * @param content
     * @param width
     * @param height
     * @param format  条形码格式
     * @return
     */
    @Nullable
    public static Bitmap createCodeBitmap(String content, int width, int height, BarcodeFormat format) {
        return createDMCodeBitmap(content, width, height, "UTF-8", "H",
                "2", Color.BLACK, Color.WHITE, false, format, false);
    }

    /**
     * 创建二维码位图 (支持自定义配置和自定义样式)
     *
     * @param content          字符串内容
     * @param width            位图宽度,要求>=0(单位:px)
     * @param height           位图高度,要求>=0(单位:px)
     * @param character_set    字符集/字符转码格式 (支持格式:{ })。传null时,zxing源码默认使用 "ISO-8859-1"
     * @param error_correction 容错级别 (支持级别:{@link  1ErrorCorrectionLevel })。传null时,zxing源码默认使用 "L"
     * @param margin           空白边距 (可修改,要求:整型且>=0), 传null时,zxing源码默认使用"4"。
     * @param color_black      黑色色块的自定义颜色值
     * @param color_white      白色色块的自定义颜色值
     * @return
     */
    @Nullable
    public static Bitmap createDMCodeBitmap(String content, int width, int height,
                                            @Nullable String character_set, @Nullable String error_correction, @Nullable String margin,
                                            @ColorInt int color_black, @ColorInt int color_white, boolean fitSizeFlag, BarcodeFormat format,
                                            boolean isShowBarCodeContent) {

        /** 1.参数合法性判断 */
        if (TextUtils.isEmpty(content)) { // 字符串内容判空
            return null;
        }

        if (width < 0 || height < 0) { // 宽和高都需要>=0
            return null;
        }

        /** 2.设置二维码相关配置,生成BitMatrix(位矩阵)对象 */
        Hashtable<EncodeHintType, String> hints = new Hashtable<>();

        if (!TextUtils.isEmpty(character_set)) {
            hints.put(EncodeHintType.CHARACTER_SET, character_set); // 字符转码格式设置
        }

        if (!TextUtils.isEmpty(error_correction)) {
            hints.put(EncodeHintType.ERROR_CORRECTION, error_correction); // 容错级别设置
        }

        if (!TextUtils.isEmpty(margin)) {
            hints.put(EncodeHintType.MARGIN, margin); // 空白边距设置
        }
        BitMatrix bitMatrix = null;
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            bitMatrix = writer.encode(content, format, width, height, hints);

            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值 */
            int[] pixels = new int[bitMatrix.getWidth() * bitMatrix.getHeight()];
            for (int y = 0; y < bitMatrix.getHeight(); y++) {
                for (int x = 0; x < bitMatrix.getWidth(); x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * bitMatrix.getWidth() + x] = color_black; // 黑色色块像素设置
                    } else {
                        pixels[y * bitMatrix.getWidth() + x] = color_white; // 白色色块像素设置
                    }
                }
            }


            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,之后返回Bitmap对象 */
            Bitmap bitmap = Bitmap.createBitmap(bitMatrix.getWidth(), bitMatrix.getHeight(), Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, bitMatrix.getWidth(), 0, 0, bitMatrix.getWidth(), bitMatrix.getHeight());
            int bitWidth = bitMatrix.getWidth();
            int bitHeight = bitMatrix.getHeight();

            if (fitSizeFlag) {
                //因为bitmap可能并不等于预先设置的width和height，需要进行等比缩放，尤其是BarcodeFormat.DATA_MATRIX格式，小的不可想象
                float wMultiple = ((float) bitWidth) / (float) width;//生成的bitmap的宽除以预期的宽
                float hMultiple = ((float) bitHeight) / (float) height;//生成的bitmap的高除以预期的高
//            Log.d(">>>", wMultiple + "--" + hMultiple);
                if (wMultiple == 1f || hMultiple == 1f) {//说明生成的条形码符合预期，不需要缩放
//                Log.d(">>>", "...re1");
                    return bitmap;
                }

                if (wMultiple > hMultiple) {//说明宽超出范围更多，以宽的比例为标准进行缩放。
                    int dstWidth = width;// bitWidth / wMultiple
                    int dstHeight = width;  // 此处是设置正方形打印输出
//                    int dstHeight = (int) (bitHeight / wMultiple);  以宽的比例为标准进行缩放。
                    bitmap = flex(bitmap, dstWidth, dstHeight);//等间采样算法进行缩放
                } else {//说明相当或高超出范围更多，以高的比例为标准进行缩放。
                    int dstHeight = height;// bitHeight / hMultiple
                    int dstWidth = height; // 此处是设置正方形打印输出
//                    int dstWidth = (int) (bitWidth / hMultiple); 以高的比例为标准进行缩放
                    bitmap = flex(bitmap, dstWidth, dstHeight);//等间采样算法进行缩放
                }
            }
            if (isShowBarCodeContent) {
                bitmap = showContent(bitmap, content);
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 等间隔采样的图像缩放
     *
     * @param bitmap    要缩放的图像对象
     * @param dstWidth  缩放后图像的宽
     * @param dstHeight 缩放后图像的高
     * @return 返回处理后的图像对象
     */
    public static Bitmap flex(Bitmap bitmap, int dstWidth, int dstHeight) {
        float wScale = (float) dstWidth / bitmap.getWidth();
        float hScale = (float) dstHeight / bitmap.getHeight();
        return flex(bitmap, wScale, hScale);
    }

    /**
     * 等间隔采样的图像缩放
     *
     * @param bitmap 要缩放的bitap对象
     * @param wScale 要缩放的横列（宽）比列
     * @param hScale 要缩放的纵行（高）比列
     * @return 返回处理后的图像对象
     */
    public static Bitmap flex(Bitmap bitmap, float wScale, float hScale) {
        if (wScale <= 0 || hScale <= 0) {
            return null;
        }
        float ii = 1 / wScale;    //采样的行间距
        float jj = 1 / hScale; //采样的列间距

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int dstWidth = (int) (wScale * width);
        int dstHeight = (int) (hScale * height);

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int[] dstPixels = new int[dstWidth * dstHeight];

        for (int j = 0; j < dstHeight; j++) {
            for (int i = 0; i < dstWidth; i++) {
                dstPixels[j * dstWidth + i] = pixels[(int) (jj * j) * width + (int) (ii * i)];
            }
        }
        System.out.println((int) ((dstWidth - 1) * ii));
        Log.d(">>>", "" + "dstPixels:" + dstWidth + " x " + dstHeight);

        Bitmap outBitmap = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888);
        outBitmap.setPixels(dstPixels, 0, dstWidth, 0, 0, dstWidth, dstHeight);

        return outBitmap;
    }


    /**
     * 显示条形的内容
     *
     * @param bCBitmap 已生成的条形码的位图
     * @param content  条形码包含的内容
     * @return 返回生成的新位图, 它是 方法{@link "与新绘制文本content的组合"}
     */
    private static Bitmap showContent(Bitmap bCBitmap, String content) {
        if (TextUtils.isEmpty(content) || null == bCBitmap) {
            return null;
        }
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);//设置填充样式
        paint.setTextSize(20);
//        paint.setTextAlign(Paint.Align.CENTER);
        //测量字符串的宽度
        int textWidth = (int) paint.measureText(content);
        Paint.FontMetrics fm = paint.getFontMetrics();
        //绘制字符串矩形区域的高度
        int textHeight = (int) (fm.bottom - fm.top);
        // x 轴的缩放比率
        float scaleRateX = bCBitmap.getWidth() / (textWidth + 1);
        paint.setTextScaleX(scaleRateX);
        //绘制文本的基线
        int baseLine = bCBitmap.getHeight() + textHeight;
        //创建一个图层，然后在这个图层上绘制bCBitmap、content
        Bitmap bitmap = Bitmap.createBitmap(bCBitmap.getWidth(), bCBitmap.getHeight() + 2 * textHeight, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas();
        canvas.drawColor(Color.WHITE);
        canvas.setBitmap(bitmap);
        canvas.drawBitmap(bCBitmap, 0, 0, null);
        canvas.drawText(content, bCBitmap.getWidth() / (content.length() * 2), baseLine, paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return bitmap;
    }


    /**
     * 创建二维码
     *
     * @param content   content
     * @param widthPix  widthPix
     * @param heightPix heightPix
     * @param logoBm    logoBm
     * @return 二维码
     */
    public static Bitmap createQRCode(String content, int widthPix, int heightPix, Bitmap logoBm) {
        try {
            if (content == null || "".equals(content)) {
                return null;
            }
            // 配置参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 容错级别
            hints.put(EncodeHintType.MARGIN, 0);    //设置图片的边距
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix,
                    heightPix, hints);
            int[] pixels = new int[widthPix * heightPix];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthPix + x] = 0xff000000;
                    } else {
                        pixels[y * widthPix + x] = 0xffffffff;
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);
            if (logoBm != null) {
                bitmap = addLogo(bitmap, logoBm);
            }
            //必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 在二维码中间添加Logo图案
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }
        if (logo == null) {
            return src;
        }
        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();
        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }
        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }
        //logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }


}
