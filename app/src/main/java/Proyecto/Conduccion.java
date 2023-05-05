package Proyecto;

public class Conduccion {
    double lat1,lat2,lon2,lon1;
    public double CalcularDistancia(Points Punto1, Points Punto2) {
        final double earthRadius = 6371; // km

        lat1 = Math.toRadians(Punto1.getLatitud());
        lon1 = Math.toRadians(Punto1.getLongitud());
        lat2 = Math.toRadians(Punto2.getLatitud());
        lon2 = Math.toRadians(Punto2.getLongitud());

        double dlon = (lon2 - lon1);
        double dlat = (lat2 - lat1);

        double sinlat = Math.sin(dlat / 2);
        double sinlon = Math.sin(dlon / 2);

        double a = (sinlat * sinlat) + Math.cos(lat1) * Math.cos(lat2) * (sinlon * sinlon);
        double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distanceInMeters = earthRadius * c * 1000;
        return distanceInMeters;
    }

    public double CalcularAngulo(Points Punto1, Points Punto2){
        lat1 = Math.toRadians(Punto1.getLatitud());
        lon1 = Math.toRadians(Punto1.getLongitud());
        lat2 = Math.toRadians(Punto2.getLatitud());
        lon2 = Math.toRadians(Punto2.getLongitud());

        double ang = 0;
        double DeltaLong = lon2 - lon1;
        ang = Math.atan2((Math.sin(DeltaLong)*Math.cos(lat2)),(Math.cos(lat1)*Math.sin(lat2))-(Math.sin(lat1)*Math.cos(lat2)*Math.cos(DeltaLong)));
        if (ang < 0){
            ang += ((360f*Math.PI)/180f); // Grados
        }
        return Math.toDegrees(ang);
    }

}
