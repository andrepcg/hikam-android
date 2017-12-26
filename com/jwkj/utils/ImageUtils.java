package com.jwkj.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import com.jwkj.global.Constants.Image;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.http.client.methods.HttpGet;

public class ImageUtils {
    private static final String TAG = "ImageUtils";

    public static Bitmap getBitmap(String imagePath) {
        return getBitmap(imagePath, 1);
    }

    public static Bitmap getBitmap(String imagePath, int sampleSize) {
        Throwable th;
        Bitmap bitmap = null;
        Options options = new Options();
        options.inDither = false;
        options.inSampleSize = sampleSize;
        RandomAccessFile file = null;
        try {
            RandomAccessFile file2 = new RandomAccessFile(imagePath, "r");
            try {
                bitmap = BitmapFactory.decodeFileDescriptor(file2.getFD(), null, options);
                if (file2 != null) {
                    try {
                        file2.close();
                    } catch (IOException e) {
                    }
                }
                file = file2;
            } catch (IOException e2) {
                file = file2;
                if (file != null) {
                    try {
                        file.close();
                    } catch (IOException e3) {
                    }
                }
                return bitmap;
            } catch (Throwable th2) {
                th = th2;
                file = file2;
                if (file != null) {
                    try {
                        file.close();
                    } catch (IOException e4) {
                    }
                }
                throw th;
            }
        } catch (IOException e5) {
            if (file != null) {
                file.close();
            }
            return bitmap;
        } catch (Throwable th3) {
            th = th3;
            if (file != null) {
                file.close();
            }
            throw th;
        }
        return bitmap;
    }

    public static Bitmap getBitmap(byte[] image, int sampleSize) {
        Options options = new Options();
        options.inDither = false;
        options.inSampleSize = sampleSize;
        return BitmapFactory.decodeByteArray(image, 0, image.length, options);
    }

    public static int getScale(Point size, int width, int height) {
        if (size.x > width || size.y > height) {
            return Math.max(Math.round(((float) size.y) / ((float) height)), Math.round(((float) size.x) / ((float) width)));
        }
        return 1;
    }

    public static Point getSize(String imagePath) {
        IOException e;
        Throwable th;
        Options options = new Options();
        options.inJustDecodeBounds = true;
        RandomAccessFile file = null;
        try {
            RandomAccessFile file2 = new RandomAccessFile(imagePath, "r");
            try {
                BitmapFactory.decodeFileDescriptor(file2.getFD(), null, options);
                Point point = new Point(options.outWidth, options.outHeight);
                if (file2 != null) {
                    try {
                        file2.close();
                    } catch (IOException e2) {
                        Log.d(TAG, e2.getMessage(), e2);
                    }
                }
                file = file2;
                return point;
            } catch (IOException e3) {
                e2 = e3;
                file = file2;
                try {
                    Log.d(TAG, e2.getMessage(), e2);
                    if (file != null) {
                        try {
                            file.close();
                        } catch (IOException e22) {
                            Log.d(TAG, e22.getMessage(), e22);
                        }
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    if (file != null) {
                        try {
                            file.close();
                        } catch (IOException e222) {
                            Log.d(TAG, e222.getMessage(), e222);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                file = file2;
                if (file != null) {
                    file.close();
                }
                throw th;
            }
        } catch (IOException e4) {
            e222 = e4;
            Log.d(TAG, e222.getMessage(), e222);
            if (file != null) {
                file.close();
            }
            return null;
        }
    }

    public static Point getSize(byte[] image) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(image, 0, image.length, options);
        return new Point(options.outWidth, options.outHeight);
    }

    public static Bitmap getBitmap(String imagePath, int width, int height) {
        return getBitmap(imagePath, getScale(getSize(imagePath), width, height));
    }

    public static Bitmap getBitmap(byte[] image, int width, int height) {
        return getBitmap(image, getScale(getSize(image), width, height));
    }

    public static Bitmap getBitmap(File image, int width, int height) {
        return getBitmap(image.getAbsolutePath(), width, height);
    }

    public static Bitmap getBitmap(File image) {
        return getBitmap(image.getAbsolutePath());
    }

    public static void setImage(String imagePath, ImageView view) {
        setImage(new File(imagePath), view);
    }

    public static void setImage(File image, ImageView view) {
        Bitmap bitmap = getBitmap(image);
        if (bitmap != null) {
            view.setImageBitmap(bitmap);
        }
    }

    public static Bitmap roundCorners(Bitmap source, float radius) {
        int width = source.getWidth();
        int height = source.getHeight();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(-1);
        Bitmap clipped = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        new Canvas(clipped).drawRoundRect(new RectF(0.0f, 0.0f, (float) width, (float) height), radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        Bitmap rounded = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(rounded);
        canvas.drawBitmap(source, 0.0f, 0.0f, null);
        canvas.drawBitmap(clipped, 0.0f, 0.0f, paint);
        source.recycle();
        clipped.recycle();
        return rounded;
    }

    public static String getAbsPath(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        String path = uri.getPath();
        if (path != null && uri.toString().toLowerCase().startsWith("file://")) {
            return path;
        }
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return path;
        }
        cursor.moveToFirst();
        int index = cursor.getColumnIndex("_data");
        if (index != -1) {
            return cursor.getString(index);
        }
        return path;
    }

    public static void saveImg(Bitmap bitmap, String path, String name) {
        IOException e;
        FileNotFoundException e2;
        Throwable th;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        FileOutputStream fos = null;
        try {
            FileOutputStream fos2 = new FileOutputStream(new File(file, name));
            try {
                bitmap.compress(CompressFormat.JPEG, 100, fos2);
                fos2.flush();
                if (fos2 != null) {
                    try {
                        fos2.close();
                        fos = fos2;
                        return;
                    } catch (IOException e3) {
                        e3.printStackTrace();
                        fos = fos2;
                        return;
                    }
                }
            } catch (FileNotFoundException e4) {
                e2 = e4;
                fos = fos2;
                try {
                    e2.printStackTrace();
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e32) {
                            e32.printStackTrace();
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e322) {
                            e322.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (IOException e5) {
                e322 = e5;
                fos = fos2;
                e322.printStackTrace();
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e3222) {
                        e3222.printStackTrace();
                    }
                }
            } catch (Throwable th3) {
                th = th3;
                fos = fos2;
                if (fos != null) {
                    fos.close();
                }
                throw th;
            }
        } catch (FileNotFoundException e6) {
            e2 = e6;
            e2.printStackTrace();
            if (fos != null) {
                fos.close();
            }
        } catch (IOException e7) {
            e3222 = e7;
            e3222.printStackTrace();
            if (fos != null) {
                fos.close();
            }
        }
    }

    public static Bitmap grayBitmap(Bitmap bitmap) {
        Bitmap bmpGrayscale = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0.0f);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        c.drawBitmap(bitmap, 0.0f, 0.0f, paint);
        bitmap.recycle();
        return bmpGrayscale;
    }

    public static int getScaleRounded(int width) {
        return (int) (((float) width) * Image.USER_HEADER_ROUND_SCALE);
    }

    public static byte[] getImageFromNetByUrl(String strUrl) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(strUrl).openConnection();
            conn.setRequestMethod(HttpGet.METHOD_NAME);
            conn.setConnectTimeout(5000);
            return readInputStream(conn.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeImageToDisk(byte[] img, String path, String name) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            FileOutputStream fops = new FileOutputStream(new File(file, name));
            fops.write(img);
            fops.flush();
            fops.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (true) {
            int len = inStream.read(buffer);
            if (len != -1) {
                outStream.write(buffer, 0, len);
            } else {
                inStream.close();
                return outStream.toByteArray();
            }
        }
    }
}
