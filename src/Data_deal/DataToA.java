package Data_deal;
import static java.lang.Math.abs;

public class DataToA {

    public static double ToA_Simple(Resvr[] resvr,int Intval){
        double Ntotal = 0;
        double Qtotal = 0;
        double Htotal = 0;
        int nh = 0;
        for (int i = 0; i < Intval; i++) {
            Ntotal += resvr[i].N;
            Qtotal += resvr[i].Qgen;
            resvr[i].Hd = (resvr[i].Zup + resvr[i + 1].Zup) / 2 - resvr[i].Zdn;
            if (resvr[i].N > 1){
                Htotal += resvr[i].Hd;
                nh++;
            }
            }
        Htotal /= nh;
        double A = Ntotal / Qtotal / Htotal;
        return A;
    }

    public static void ToProfit(Resvr[] resvr,int Intval){
        for (int i = 0; i < Intval; i++) {
            if (resvr[i].t >= 0 && resvr[i].t <= 7) {
                resvr[i].profit = 1000 * 0.14 * resvr[i].N;
            }
            else if ((resvr[i].t >= 8 && resvr[i].t <= 11) || (resvr[i].t >= 17 && resvr[i].t <= 20)) {
                resvr[i].profit = 1000 * 0.54 * resvr[i].N;
            }
            else {
                resvr[i].profit = 1000 * 0.32 * resvr[i].N;
            }

        }
    }
    public static  void AToQout(Resvr[] resvr,int Intval,double A,QZ[] qz,int Nqz){
        for (int i = 0; i < Intval; i++) {
            seek sk = new seek();
            if (resvr[i].N != 0){
                double h = 0;
                int loop = 0;
                while(abs(h - resvr[i].Hd) > 0.1){
                    if (h > 0)
                    {
                        resvr[i].Hd = h;
                    }
                    resvr[i].Qout = resvr[i].N / A / resvr[i].Hd;
                    h = (resvr[i].Zup + resvr[i + 1].Zup) / 2 - sk.QtoZ(resvr[i].Qout,qz,Nqz);
                    loop++;
                    if (loop > 5)
                    {
                        break;
                    }
                }
                resvr[i].Qout = resvr[i].N / A / resvr[i].Hd;
            }
            else
            {
                resvr[i].Qout = 0;
            }
        }
    }
    public static void QoutToQin(Resvr[] resvr,Resvr[] SimuR,int Intval,ZV[] zv,int Nzv,double[] battery){
        for (int i = 0; i < Intval; i++) {
            seek sk = new seek();
            resvr[i].Qin = (sk.ZtoV(resvr[i + 1].Zup,zv,Nzv) - sk.ZtoV(resvr[i].Zup,zv,Nzv)) / 3600 + resvr[i].Qout;
            SimuR[i].Qin = resvr[i].Qin;
//            SimuR[i].Qin = resvr[i].Qin + battery[i];
//            System.out.println(SimuR[i].Qin);
        }
    }

    public static void QinToQave(Resvr[] resvr,Resvr[] SimuR,int Intval,double[] battery)
    {
        int n = 0;
        int ini = 0;
        double Qtotal = 0;
        for (int i = 0; i < Intval; i++)
        {
            if (n==0){
                ini = i;
            }
            n++;
            Qtotal += resvr[i].Qin;
            if(resvr[i].t == 23){
                for (int j = 0; j < n; j++)
                {
                    resvr[j+ini].Qin = Qtotal/n;
                }
                n=0;
                Qtotal=0;
            }
        }
        for (int i = 0; i < Intval; i++)
        {
            SimuR[i].Qin = resvr[i].Qin + battery[i];
        }
    }

}
