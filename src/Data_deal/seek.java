package Data_deal;

public class seek {
    /*
    插值计算（上水位-库容、库容-上水位、流量-水位）
     */
    public static double ZtoV(double Z,ZV[] zv,int Nzv){
        if (Z <= zv[0].Z){
            return zv[0].V;
        }
        else if (Z >= zv[Nzv - 1].Z) {
            return zv[Nzv - 1].V;
        }
        else {
            int indx = 0;
            for (int i = 0; i < Nzv - 1; i++) {
                if (Z >= zv[i].Z && Z < zv[i + 1].Z) {
                    indx = i;
                    break;
                }
            }
            return zv[indx].V + (zv[indx + 1].V - zv[indx].V) * (Z - zv[indx].Z) / (zv[indx + 1].Z - zv[indx].Z);
        }
    }
    public static double VtoZ(double V,ZV[] zv,int Nzv){
        if (V <= zv[0].V) {
            return zv[0].Z;
        }
        else if (V >= zv[Nzv - 1].V) {
            return zv[Nzv - 1].Z;
        }
        else {
            int indx = 0;
            for (int i = 0; i < Nzv - 1; i++) {
                if (V >= zv[i].V && V < zv[i + 1].V) {
                    indx = i;
                    break;
                }
            }
            return (zv[indx].Z +  (V - zv[indx].V) / (zv[indx + 1].V - zv[indx].V));
        }
    }
    public static double QtoZ(double Q,QZ[] qz,int Nqz){
        if (Q <= qz[0].Q) {
            return qz[0].Z;
        }
        else if (Q >= qz[Nqz - 1].Q) {
            return qz[Nqz - 1].Z;
        }
        else {
            int indx = 0;
            for (int i = 0; i < Nqz - 1; i++) {
                if (Q >= qz[i].Q && Q < qz[i + 1].Q) {
                    indx = i;
                    break;
                }

            }
            return qz[indx].Z + (qz[indx + 1].Z - qz[indx].Z) * (Q - qz[indx].Q) / (qz[indx + 1].Q - qz[indx].Q);
        }
    }
}
