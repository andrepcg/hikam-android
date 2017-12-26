package com.tencent.bugly.crashreport.crash.anr;

import com.tencent.bugly.proguard.C0757w;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* compiled from: BUGLY */
public class TraceFileHelper {

    /* compiled from: BUGLY */
    public static class C0712a {
        public long f248a;
        public String f249b;
        public long f250c;
        public Map<String, String[]> f251d;
    }

    /* compiled from: BUGLY */
    public interface C0713b {
        boolean mo2270a(long j);

        boolean mo2271a(long j, long j2, String str);

        boolean mo2272a(String str, int i, String str2, String str3);
    }

    public static C0712a readTargetDumpInfo(String str, String str2, final boolean z) {
        if (str == null || str2 == null) {
            return null;
        }
        final C0712a c0712a = new C0712a();
        readTraceFile(str2, new C0713b() {
            public final boolean mo2272a(String str, int i, String str2, String str3) {
                C0757w.m460c("new thread %s", str);
                if (c0712a.f248a > 0 && c0712a.f250c > 0 && c0712a.f249b != null) {
                    if (c0712a.f251d == null) {
                        c0712a.f251d = new HashMap();
                    }
                    c0712a.f251d.put(str, new String[]{str2, str3, i});
                }
                return true;
            }

            public final boolean mo2271a(long j, long j2, String str) {
                C0757w.m460c("new process %s", str);
                if (!str.equals(str)) {
                    return true;
                }
                c0712a.f248a = j;
                c0712a.f249b = str;
                c0712a.f250c = j2;
                if (z) {
                    return true;
                }
                return false;
            }

            public final boolean mo2270a(long j) {
                C0757w.m460c("process end %d", Long.valueOf(j));
                if (c0712a.f248a <= 0 || c0712a.f250c <= 0 || c0712a.f249b == null) {
                    return true;
                }
                return false;
            }
        });
        if (c0712a.f248a <= 0 || c0712a.f250c <= 0 || c0712a.f249b == null) {
            return null;
        }
        return c0712a;
    }

    public static C0712a readFirstDumpInfo(String str, final boolean z) {
        if (str == null) {
            C0757w.m462e("path:%s", str);
            return null;
        }
        final C0712a c0712a = new C0712a();
        readTraceFile(str, new C0713b() {
            public final boolean mo2272a(String str, int i, String str2, String str3) {
                C0757w.m460c("new thread %s", str);
                if (c0712a.f251d == null) {
                    c0712a.f251d = new HashMap();
                }
                c0712a.f251d.put(str, new String[]{str2, str3, i});
                return true;
            }

            public final boolean mo2271a(long j, long j2, String str) {
                C0757w.m460c("new process %s", str);
                c0712a.f248a = j;
                c0712a.f249b = str;
                c0712a.f250c = j2;
                if (z) {
                    return true;
                }
                return false;
            }

            public final boolean mo2270a(long j) {
                C0757w.m460c("process end %d", Long.valueOf(j));
                return false;
            }
        });
        if (c0712a.f248a > 0 && c0712a.f250c > 0 && c0712a.f249b != null) {
            return c0712a;
        }
        C0757w.m462e("first dump error %s", c0712a.f248a + " " + c0712a.f250c + " " + c0712a.f249b);
        return null;
    }

    public static void readTraceFile(String str, C0713b c0713b) {
        Throwable e;
        if (str != null && c0713b != null) {
            File file = new File(str);
            if (file.exists()) {
                file.lastModified();
                file.length();
                BufferedReader bufferedReader = null;
                BufferedReader bufferedReader2;
                try {
                    bufferedReader2 = new BufferedReader(new FileReader(file));
                    try {
                        Pattern compile = Pattern.compile("-{5}\\spid\\s\\d+\\sat\\s\\d+-\\d+-\\d+\\s\\d{2}:\\d{2}:\\d{2}\\s-{5}");
                        Pattern compile2 = Pattern.compile("-{5}\\send\\s\\d+\\s-{5}");
                        Pattern compile3 = Pattern.compile("Cmd\\sline:\\s(\\S+)");
                        Pattern compile4 = Pattern.compile("\".+\"\\s(daemon\\s){0,1}prio=\\d+\\stid=\\d+\\s.*");
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                        while (true) {
                            Object[] a = m183a(bufferedReader2, compile);
                            if (a != null) {
                                String[] split = a[1].toString().split("\\s");
                                long parseLong = Long.parseLong(split[2]);
                                long time = simpleDateFormat.parse(split[4] + " " + split[5]).getTime();
                                a = m183a(bufferedReader2, compile3);
                                if (a == null) {
                                    try {
                                        bufferedReader2.close();
                                        return;
                                    } catch (Throwable e2) {
                                        if (!C0757w.m457a(e2)) {
                                            e2.printStackTrace();
                                            return;
                                        }
                                        return;
                                    }
                                }
                                Matcher matcher = compile3.matcher(a[1].toString());
                                matcher.find();
                                matcher.group(1);
                                if (c0713b.mo2271a(parseLong, time, matcher.group(1))) {
                                    while (true) {
                                        a = m183a(bufferedReader2, compile4, compile2);
                                        if (a != null) {
                                            if (a[0] != compile4) {
                                                break;
                                            }
                                            CharSequence obj = a[1].toString();
                                            Matcher matcher2 = Pattern.compile("\".+\"").matcher(obj);
                                            matcher2.find();
                                            String group = matcher2.group();
                                            group = group.substring(1, group.length() - 1);
                                            obj.contains("NATIVE");
                                            matcher = Pattern.compile("tid=\\d+").matcher(obj);
                                            matcher.find();
                                            String group2 = matcher.group();
                                            c0713b.mo2272a(group, Integer.parseInt(group2.substring(group2.indexOf("=") + 1)), m182a(bufferedReader2), m184b(bufferedReader2));
                                        } else {
                                            break;
                                        }
                                    }
                                    if (!c0713b.mo2270a(Long.parseLong(a[1].toString().split("\\s")[2]))) {
                                        try {
                                            bufferedReader2.close();
                                            return;
                                        } catch (Throwable e22) {
                                            if (!C0757w.m457a(e22)) {
                                                e22.printStackTrace();
                                                return;
                                            }
                                            return;
                                        }
                                    }
                                } else {
                                    try {
                                        bufferedReader2.close();
                                        return;
                                    } catch (Throwable e222) {
                                        if (!C0757w.m457a(e222)) {
                                            e222.printStackTrace();
                                            return;
                                        }
                                        return;
                                    }
                                }
                            }
                            try {
                                bufferedReader2.close();
                                return;
                            } catch (Throwable e2222) {
                                if (!C0757w.m457a(e2222)) {
                                    e2222.printStackTrace();
                                    return;
                                }
                                return;
                            }
                        }
                    } catch (Exception e3) {
                        e2222 = e3;
                        bufferedReader = bufferedReader2;
                    } catch (Throwable th) {
                        e2222 = th;
                    }
                } catch (Exception e4) {
                    e2222 = e4;
                    try {
                        if (!C0757w.m457a(e2222)) {
                            e2222.printStackTrace();
                        }
                        C0757w.m461d("trace open fail:%s : %s", e2222.getClass().getName(), e2222.getMessage());
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (Throwable e22222) {
                                if (!C0757w.m457a(e22222)) {
                                    e22222.printStackTrace();
                                }
                            }
                        }
                    } catch (Throwable th2) {
                        e22222 = th2;
                        bufferedReader2 = bufferedReader;
                        if (bufferedReader2 != null) {
                            try {
                                bufferedReader2.close();
                            } catch (Throwable e5) {
                                if (!C0757w.m457a(e5)) {
                                    e5.printStackTrace();
                                }
                            }
                        }
                        throw e22222;
                    }
                } catch (Throwable th3) {
                    e22222 = th3;
                    bufferedReader2 = null;
                    if (bufferedReader2 != null) {
                        bufferedReader2.close();
                    }
                    throw e22222;
                }
            }
        }
    }

    private static Object[] m183a(BufferedReader bufferedReader, Pattern... patternArr) throws IOException {
        if (bufferedReader == null || patternArr == null) {
            return null;
        }
        while (true) {
            CharSequence readLine = bufferedReader.readLine();
            if (readLine == null) {
                return null;
            }
            for (Pattern matcher : patternArr) {
                if (matcher.matcher(readLine).matches()) {
                    return new Object[]{patternArr[r1], readLine};
                }
            }
        }
    }

    private static String m182a(BufferedReader bufferedReader) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < 3; i++) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                return null;
            }
            stringBuffer.append(readLine + "\n");
        }
        return stringBuffer.toString();
    }

    private static String m184b(BufferedReader bufferedReader) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine != null && readLine.trim().length() > 0) {
                stringBuffer.append(readLine + "\n");
            }
        }
        return stringBuffer.toString();
    }
}
