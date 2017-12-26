package com.google.zxing.client.result;

import com.google.zxing.Result;

final class GeoResultParser extends ResultParser {
    private GeoResultParser() {
    }

    public static GeoParsedResult parse(Result result) {
        String rawText = result.getText();
        if (rawText == null || (!rawText.startsWith("geo:") && !rawText.startsWith("GEO:"))) {
            return null;
        }
        String query;
        String geoURIWithoutQuery;
        int queryStart = rawText.indexOf(63, 4);
        if (queryStart < 0) {
            query = null;
            geoURIWithoutQuery = rawText.substring(4);
        } else {
            query = rawText.substring(queryStart + 1);
            geoURIWithoutQuery = rawText.substring(4, queryStart);
        }
        int latitudeEnd = geoURIWithoutQuery.indexOf(44);
        if (latitudeEnd < 0) {
            return null;
        }
        int longitudeEnd = geoURIWithoutQuery.indexOf(44, latitudeEnd + 1);
        try {
            double latitude = Double.parseDouble(geoURIWithoutQuery.substring(0, latitudeEnd));
            if (latitude > 90.0d || latitude < -90.0d) {
                return null;
            }
            double longitude;
            double altitude;
            if (longitudeEnd < 0) {
                longitude = Double.parseDouble(geoURIWithoutQuery.substring(latitudeEnd + 1));
                altitude = 0.0d;
            } else {
                longitude = Double.parseDouble(geoURIWithoutQuery.substring(latitudeEnd + 1, longitudeEnd));
                altitude = Double.parseDouble(geoURIWithoutQuery.substring(longitudeEnd + 1));
            }
            if (longitude > 180.0d || longitude < -180.0d || altitude < 0.0d) {
                return null;
            }
            return new GeoParsedResult(latitude, longitude, altitude, query);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
