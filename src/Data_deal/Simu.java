package Data_deal;
import static java.lang.Math.abs;
public class Simu {
    public static void simulate(double Zmin,double Zflat,double Zmax,double Nmax,double A,Resvr[] SimuR,ZV[] zv,int Nzv,int Intval,int Nqz,QZ[] qz){
        seek sk = new seek();
        double vmin = sk.ZtoV(Zmin,zv,Nzv);
        double vflat = sk.ZtoV(Zflat,zv,Nzv);
        double vmax = sk.ZtoV(Zmax,zv,Nzv);
        int flag = 1;
        for (int i = 0; i < Intval; i++) {
            //蓄水，多余水发电
            if (SimuR[i].t < 8 || (SimuR[i].t >= 12 && SimuR[i].t <= 16)) {
                flag = 1;
                // 判断时段末水位是否大于正常蓄水位
                if (sk.ZtoV(SimuR[i].Zup,zv,Nzv) + SimuR[i].Qin * 3600 > vflat) {
                    // 大于则末水位为正常蓄水位，多余水发电
                    SimuR[i + 1].Zup = Zflat;
                    SimuR[i].Qout = SimuR[i].Qin - (vflat - sk.ZtoV(SimuR[i].Zup,zv,Nzv)) / 3600;
                    SimuR[i].Qgen = SimuR[i].Qout;
                    // 水头
                    SimuR[i].Hd = (SimuR[i].Zup + SimuR[i + 1].Zup) / 2 - sk.QtoZ(SimuR[i].Qout,qz,Nqz);

                    // 水头小于8，停机
                    if(SimuR[i].Hd >= 8){
                        SimuR[i].N = A * SimuR[i].Qgen * SimuR[i].Hd;
                        if (SimuR[i].N > Nmax) {
                            SimuR[i].N = Nmax;
                            SimuR[i].Qgen = Nmax / A / SimuR[i].Hd;
                        }
                    }else{
                        SimuR[i].Qgen = 0;
                        SimuR[i].N = 0;
                    }

                }
                else {// 小于则蓄水
                    SimuR[i + 1].Zup = sk.VtoZ(sk.ZtoV(SimuR[i].Zup,zv,Nzv) + SimuR[i].Qin * 3600,zv,Nzv);
                    SimuR[i].Qout = 0;
                    SimuR[i].N = 0;
                    SimuR[i].Qgen = 0;
                }
            }

            // 来多少发多少
            else if (SimuR[i].t >= 21 && SimuR[i].t <= 23) {
                flag = 1;
                SimuR[i + 1].Zup = SimuR[i].Zup;
                SimuR[i].Qout = SimuR[i].Qin;
                SimuR[i].Qgen = SimuR[i].Qout;
                // 水头
                SimuR[i].Hd = (SimuR[i].Zup + SimuR[i + 1].Zup) / 2 - sk.QtoZ(SimuR[i].Qout,qz,Nqz);

                if(SimuR[i].Hd > 8) {
                    SimuR[i].N = A * SimuR[i].Qgen * SimuR[i].Hd;
                    if(SimuR[i].N > Nmax) {
                        double h = 14;
                        SimuR[i].Qout = SimuR[i].Qgen = Nmax / A / h;
                        SimuR[i + 1].Zup = sk.VtoZ(sk.ZtoV(SimuR[i].Zup,zv,Nzv) + (SimuR[i].Qin - SimuR[i].Qout) * 3600,zv,Nzv);
                        SimuR[i].Hd = (SimuR[i].Zup + SimuR[i + 1].Zup) / 2 - sk.QtoZ(SimuR[i].Qout,qz,Nqz);
                        int loop = 0;
                        while (abs(SimuR[i].Hd - h) > 0.1) {
                            h = SimuR[i].Hd;
                            SimuR[i].Qout = SimuR[i].Qgen = Nmax / A / h;
                            // 水量平衡方程计算下时段水位
                            SimuR[i + 1].Zup = sk.VtoZ(sk.ZtoV(SimuR[i].Zup,zv,Nzv) + (SimuR[i].Qin - SimuR[i].Qout) * 3600,zv,Nzv);
                            SimuR[i].Hd = (SimuR[i].Zup + SimuR[i + 1].Zup) / 2 - sk.QtoZ(SimuR[i].Qout,qz,Nqz);
                            loop++;
                            if (loop > 5) {
                                break;
                            }
                        }

                        // 正常蓄水位控制
                        double Q = (sk.ZtoV(SimuR[i].Zup,zv,Nzv) + SimuR[i].Qin * 3600 - sk.ZtoV(Zflat,zv,Nzv)) / 3600;

                        if(Q > SimuR[i].Qout) {
                            SimuR[i].Qout = Q;
                            SimuR[i + 1].Zup = Zflat;
                            SimuR[i].Hd = (SimuR[i].Zup + SimuR[i + 1].Zup) / 2 - sk.QtoZ(SimuR[i].Qout,qz,Nqz);
                        }

                        SimuR[i].N = Nmax;
                        if(SimuR[i].Hd < 8 || SimuR[i].Qin > 12000) {
                            SimuR[i].N = 0;
                            SimuR[i].Qgen = 0;
                            if (sk.ZtoV(SimuR[i].Zup,zv,Nzv) + SimuR[i].Qin * 3600 > vflat) {
                                SimuR[i].Qout = SimuR[i].Qin - (vflat - sk.ZtoV(SimuR[i].Zup,zv,Nzv)) / 3600;
                            }else {
                                SimuR[i].Qout = 0;
                            }
                        }

                        // 死水位控制
                        if (SimuR[i + 1].Zup < Zmin) {
                            SimuR[i].Qout = 0;
                            SimuR[i + 1].Zup = sk.VtoZ(sk.ZtoV(SimuR[i].Zup,zv,Nzv) + (SimuR[i].Qin - SimuR[i].Qout) * 3600,zv,Nzv);
                            SimuR[i].N = 0;
                        }
                    }
                }else{// 水头小于8m
                    if (sk.ZtoV(SimuR[i].Zup,zv,Nzv) + SimuR[i].Qin * 3600 > vflat) {
                        SimuR[i + 1].Zup = Zflat;
                        SimuR[i].Qout = SimuR[i].Qin - (vflat - sk.ZtoV(SimuR[i].Zup,zv,Nzv)) / 3600;
                        // SimuR[i].Hd = (SimuR[i].Zup + SimuR[i + 1].Zup) / 2 - sk.QtoZ(SimuR[i].Qout);
                        SimuR[i].N = 0;
                        SimuR[i].Qgen = 0;
                    } else {
                        SimuR[i + 1].Zup = sk.VtoZ(sk.ZtoV(SimuR[i].Zup,zv,Nzv) + SimuR[i].Qin * 3600,zv,Nzv);
                        SimuR[i].Qout = 0;
                        SimuR[i].Qgen = 0;
                        SimuR[i].N = 0;
                    }
                }

                if(SimuR[i].Qout < 0){
                    SimuR[i].Qout = 0;
                    SimuR[i].Qgen = 0;
                    SimuR[i].N = 0;
                    SimuR[i + 1].Zup = sk.VtoZ(sk.ZtoV(SimuR[i].Zup,zv,Nzv) + SimuR[i].Qin * 3600,zv,Nzv);
                }
            }

            else {
                if (flag == 1) { // 满发
                    double h = 14;
                    SimuR[i].Qgen = Nmax / A / h;
                    SimuR[i].Qout = SimuR[i].Qgen;
                    SimuR[i + 1].Zup = sk.VtoZ(sk.ZtoV(SimuR[i].Zup,zv,Nzv) + (SimuR[i].Qin - SimuR[i].Qout) * 3600,zv,Nzv);
//                    System.out.println(SimuR[i + 1].Zup);
                    SimuR[i].Hd = (SimuR[i].Zup + SimuR[i + 1].Zup) / 2 - sk.QtoZ(SimuR[i].Qout,qz,Nqz);
                    int loop = 0;
                    while (abs(SimuR[i].Hd - h) > 0.1 ) {
                        h = SimuR[i].Hd;
                        SimuR[i].Qout = SimuR[i].Qgen = Nmax / A / h;
                        SimuR[i + 1].Zup = sk.VtoZ(sk.ZtoV(SimuR[i].Zup,zv,Nzv) + (SimuR[i].Qin - SimuR[i].Qout) * 3600,zv,Nzv);
                        SimuR[i].Hd = (SimuR[i].Zup + SimuR[i + 1].Zup) / 2 - sk.QtoZ(SimuR[i].Qout,qz,Nqz);
                        loop++;
                        if (loop > 5) {
                            break;
                        }
                    }

                    double Q = (sk.ZtoV(SimuR[i].Zup,zv,Nzv) + SimuR[i].Qin * 3600 - sk.ZtoV(Zflat,zv,Nzv)) / 3600;
                    if(Q > SimuR[i].Qout) {
                        SimuR[i].Qout = Q;
                        SimuR[i + 1].Zup = Zflat;
                        SimuR[i].Hd = (SimuR[i].Zup + SimuR[i + 1].Zup) / 2 - sk.QtoZ(SimuR[i].Qout,qz,Nqz);
                    }

                    SimuR[i].N = Nmax;


                    if(SimuR[i].Hd < 8 || SimuR[i].Qin > 12000) {
                        SimuR[i].N = 0;
                        SimuR[i].Qgen = 0;
                        if (sk.ZtoV(SimuR[i].Zup,zv,Nzv) + SimuR[i].Qin * 3600 > vflat) {
                            SimuR[i].Qout = SimuR[i].Qin - (vflat - sk.ZtoV(SimuR[i].Zup,zv,Nzv)) / 3600;
                        }else {
                            SimuR[i].Qout = 0;
                        }
                    }

                    // 死水位控制
                    if (SimuR[i + 1].Zup < Zmin) {
                        SimuR[i].Qout = 0;
                        SimuR[i + 1].Zup = sk.VtoZ(sk.ZtoV(SimuR[i].Zup,zv,Nzv) + (SimuR[i].Qin - SimuR[i].Qout) * 3600,zv,Nzv);
                        SimuR[i].N = 0;
                    }
                }
                else {
                    SimuR[i].Qout = 0;
                    SimuR[i + 1].Zup = sk.VtoZ(sk.ZtoV(SimuR[i].Zup,zv,Nzv) + (SimuR[i].Qin - SimuR[i].Qout) * 3600,zv,Nzv);
                    SimuR[i].N = 0;
                }
                double v = 0;
                int timeFlag = (SimuR[i].t < 12) ? 12 : 21;
                for (int j = 0; j < timeFlag - SimuR[i].t; j++) {
                    v += SimuR[i + j].Qin * 3600;
                }
                if (v + sk.ZtoV(SimuR[i].Zup,zv,Nzv) < vmin) {
                    flag = 0;
                }
            }

            if (SimuR[i].t >= 0 && SimuR[i].t <= 7) {//低谷0.14
                SimuR[i].profit = 1000 * 0.14 * SimuR[i].N;
            }
            else if ((SimuR[i].t >= 8 && SimuR[i].t <= 11) || (SimuR[i].t >= 17 && SimuR[i].t <= 20)) {//高峰0.54
                SimuR[i].profit = 1000 * 0.54 * SimuR[i].N;
            }
            else {//平峰0.32
                SimuR[i].profit = 1000 * 0.32 * SimuR[i].N;
            }


        }
    }

    public static void simulate23m(double Zmin,double Zflat,double Zmax,double Nmax,double A,Resvr[] SimuR,ZV[] zv,int Nzv,int Intval,int Nqz,QZ[] qz){

    }
}
