package Data_deal;

import jdk.swing.interop.SwingInterOpUtils;

public class fcj_test {
    public static void main(String[] args) {
        double A;//N=AQH

        double Zmin = 21.5;
//        double Zmin = 21.7;

        double Zmax = 24.7;

      double Zflat = 23.0;
//        double Zflat = 22.8;

        double Nmax = 360.0;
//        int Intval = 8760;
        int Intval = 745;
        int Nzv = 20;

        ZV[] zv = new ZV[20];
        int Nqz = 10;
        QZ[] qz = new QZ[10];
        Resvr[] resvr = new Resvr[9000];
        Resvr[] sIMUR = new Resvr[9000];
        double[] battery = new double[9000];
        IOPUT io = new IOPUT();
        DataToA da = new DataToA();
        Simu si = new Simu();
        io.ReadNzv(zv);
        io.ReadNqz(qz);
        io.Readfrom(Intval, resvr, sIMUR,battery);

        A = da.ToA_Simple(resvr, Intval);
        da.QoutToQin(resvr, sIMUR, Intval, zv, Nzv,battery);
        da.ToProfit(resvr, Intval);
        io.outQio(Intval, resvr);
        io.OutN(Intval,A, resvr, qz, Nqz);
        si.simulate(Zmin,Zflat,Zmax,Nmax,A,sIMUR,zv,Nzv,Intval,Nqz,qz);
        io.outPut(Intval, sIMUR);
    }

}
