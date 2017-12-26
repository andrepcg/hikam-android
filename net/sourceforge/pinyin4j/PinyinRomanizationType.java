package net.sourceforge.pinyin4j;

class PinyinRomanizationType {
    static final PinyinRomanizationType GWOYEU_ROMATZYH = new PinyinRomanizationType("Gwoyeu");
    static final PinyinRomanizationType HANYU_PINYIN = new PinyinRomanizationType("Hanyu");
    static final PinyinRomanizationType MPS2_PINYIN = new PinyinRomanizationType("MPSII");
    static final PinyinRomanizationType TONGYONG_PINYIN = new PinyinRomanizationType("Tongyong");
    static final PinyinRomanizationType WADEGILES_PINYIN = new PinyinRomanizationType("Wade");
    static final PinyinRomanizationType YALE_PINYIN = new PinyinRomanizationType("Yale");
    protected String tagName;

    protected PinyinRomanizationType(String str) {
        setTagName(str);
    }

    String getTagName() {
        return this.tagName;
    }

    protected void setTagName(String str) {
        this.tagName = str;
    }
}
