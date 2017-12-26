package net.sourceforge.pinyin4j.format;

public class HanyuPinyinCaseType {
    public static final HanyuPinyinCaseType LOWERCASE = new HanyuPinyinCaseType("LOWERCASE");
    public static final HanyuPinyinCaseType UPPERCASE = new HanyuPinyinCaseType("UPPERCASE");
    protected String name;

    protected HanyuPinyinCaseType(String str) {
        setName(str);
    }

    public String getName() {
        return this.name;
    }

    protected void setName(String str) {
        this.name = str;
    }
}
