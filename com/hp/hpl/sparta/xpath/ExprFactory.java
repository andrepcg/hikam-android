package com.hp.hpl.sparta.xpath;

import java.io.IOException;

public class ExprFactory {
    static BooleanExpr createExpr(XPath xPath, SimpleStreamTokenizer simpleStreamTokenizer) throws XPathException, IOException {
        String str;
        switch (simpleStreamTokenizer.ttype) {
            case -3:
                if (!simpleStreamTokenizer.sval.equals("text")) {
                    throw new XPathException(xPath, "at beginning of expression", simpleStreamTokenizer, "text()");
                } else if (simpleStreamTokenizer.nextToken() != 40) {
                    throw new XPathException(xPath, "after text", simpleStreamTokenizer, "(");
                } else if (simpleStreamTokenizer.nextToken() != 41) {
                    throw new XPathException(xPath, "after text(", simpleStreamTokenizer, ")");
                } else {
                    switch (simpleStreamTokenizer.nextToken()) {
                        case 33:
                            simpleStreamTokenizer.nextToken();
                            if (simpleStreamTokenizer.ttype != 61) {
                                throw new XPathException(xPath, "after !", simpleStreamTokenizer, "=");
                            }
                            simpleStreamTokenizer.nextToken();
                            if (simpleStreamTokenizer.ttype == 34 || simpleStreamTokenizer.ttype == 39) {
                                str = simpleStreamTokenizer.sval;
                                simpleStreamTokenizer.nextToken();
                                return new TextNotEqualsExpr(str);
                            }
                            throw new XPathException(xPath, "right hand side of !=", simpleStreamTokenizer, "quoted string");
                        case 61:
                            simpleStreamTokenizer.nextToken();
                            if (simpleStreamTokenizer.ttype == 34 || simpleStreamTokenizer.ttype == 39) {
                                str = simpleStreamTokenizer.sval;
                                simpleStreamTokenizer.nextToken();
                                return new TextEqualsExpr(str);
                            }
                            throw new XPathException(xPath, "right hand side of equals", simpleStreamTokenizer, "quoted string");
                        default:
                            return TextExistsExpr.INSTANCE;
                    }
                }
            case -2:
                int i = simpleStreamTokenizer.nval;
                simpleStreamTokenizer.nextToken();
                return new PositionEqualsExpr(i);
            case 64:
                if (simpleStreamTokenizer.nextToken() != -3) {
                    throw new XPathException(xPath, "after @", simpleStreamTokenizer, HttpPostBodyUtil.NAME);
                }
                String str2 = simpleStreamTokenizer.sval;
                int parseInt;
                switch (simpleStreamTokenizer.nextToken()) {
                    case 33:
                        simpleStreamTokenizer.nextToken();
                        if (simpleStreamTokenizer.ttype != 61) {
                            throw new XPathException(xPath, "after !", simpleStreamTokenizer, "=");
                        }
                        simpleStreamTokenizer.nextToken();
                        if (simpleStreamTokenizer.ttype == 34 || simpleStreamTokenizer.ttype == 39) {
                            str = simpleStreamTokenizer.sval;
                            simpleStreamTokenizer.nextToken();
                            return new AttrNotEqualsExpr(str2, str);
                        }
                        throw new XPathException(xPath, "right hand side of !=", simpleStreamTokenizer, "quoted string");
                    case 60:
                        simpleStreamTokenizer.nextToken();
                        if (simpleStreamTokenizer.ttype == 34 || simpleStreamTokenizer.ttype == 39) {
                            parseInt = Integer.parseInt(simpleStreamTokenizer.sval);
                        } else if (simpleStreamTokenizer.ttype == -2) {
                            parseInt = simpleStreamTokenizer.nval;
                        } else {
                            throw new XPathException(xPath, "right hand side of less-than", simpleStreamTokenizer, "quoted string or number");
                        }
                        simpleStreamTokenizer.nextToken();
                        return new AttrLessExpr(str2, parseInt);
                    case 61:
                        simpleStreamTokenizer.nextToken();
                        if (simpleStreamTokenizer.ttype == 34 || simpleStreamTokenizer.ttype == 39) {
                            str = simpleStreamTokenizer.sval;
                            simpleStreamTokenizer.nextToken();
                            return new AttrEqualsExpr(str2, str);
                        }
                        throw new XPathException(xPath, "right hand side of equals", simpleStreamTokenizer, "quoted string");
                    case 62:
                        simpleStreamTokenizer.nextToken();
                        if (simpleStreamTokenizer.ttype == 34 || simpleStreamTokenizer.ttype == 39) {
                            parseInt = Integer.parseInt(simpleStreamTokenizer.sval);
                        } else if (simpleStreamTokenizer.ttype == -2) {
                            parseInt = simpleStreamTokenizer.nval;
                        } else {
                            throw new XPathException(xPath, "right hand side of greater-than", simpleStreamTokenizer, "quoted string or number");
                        }
                        simpleStreamTokenizer.nextToken();
                        return new AttrGreaterExpr(str2, parseInt);
                    default:
                        return new AttrExistsExpr(str2);
                }
            default:
                throw new XPathException(xPath, "at beginning of expression", simpleStreamTokenizer, "@, number, or text()");
        }
    }
}
