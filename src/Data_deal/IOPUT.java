package Data_deal;
import java.io.*;



public class IOPUT
{
    public static void Readfrom(int Intval,Resvr[] resvr,Resvr[] SimuR,double[] battery)
    {
       String pathname = "src//OpProcess.txt";
//       String pathname = "src//a.txt";
        for (int i = 0; i < 9000; i++) {
            resvr[i] = new Resvr();
            SimuR[i] = new Resvr();
        }
       try(FileReader reader = new FileReader(pathname);
           BufferedReader br = new BufferedReader(reader))
       {
           String line;
           for (int i = 0; i <Intval ; i++) {
               line = br.readLine();
               String[] s = line.split("\t");
               resvr[i].t = Integer.parseInt(s[0].trim());
               resvr[i].Zup = Double.parseDouble(s[1]);
               resvr[i].Zdn = Double.parseDouble(s[2]);
               resvr[i].N = Double.parseDouble(s[3]);
               resvr[i].Qout = Double.parseDouble(s[4]);
               resvr[i].Qgen = Double.parseDouble(s[5]);
               battery[i] = Double.parseDouble(s[6]);
               SimuR[i].t = resvr[i].t;
               }
           SimuR[0].Zup = resvr[0].Zup;

       }catch (IOException e){
           e.printStackTrace();
       }
    }
    public static void ReadNzv(ZV[] zv){
        String pathname = "src//PWCurve1.txt";
        for (int i = 0; i < 20; i++) {
            zv[i] = new ZV();
        }
        try(FileReader reader = new FileReader(pathname);
            BufferedReader br = new BufferedReader(reader)){
            String line;
            for (int i = 0; i < 20; i++) {
                line = br.readLine();
                String[] s = line.split("\t");
                zv[i].Z = Double.parseDouble(s[0]);
                zv[i].V = Double.parseDouble(s[1]);
                zv[i].V *= 100000000;
            }

        }catch (IOException e){
            e.printStackTrace();
    }}
    public static void ReadNqz(QZ[] qz){
        String pathname = "src//PWCurve2.txt";
        for (int i = 0; i < 10; i++) {
            qz[i] = new QZ();
        }
        try(FileReader reader = new FileReader(pathname);
            BufferedReader br = new BufferedReader(reader)){
            String line;
            for (int i = 0; i < 10; i++) {
                line = br.readLine();
                String[] s = line.split("\t");
                qz[i].Z = Double.parseDouble(s[0]);
                qz[i].Q = Double.parseDouble(s[1]);
            }
        }catch (IOException e){
            e.printStackTrace();
        }}
    public static void outQio(int Intval,Resvr[] resvr){
        String pathname = "src//Qio.txt";
        try(FileWriter writer = new FileWriter(pathname);
            BufferedWriter bw = new BufferedWriter(writer)){
            for (int i = 0; i < Intval; i++) {
                String a = String.valueOf(resvr[i].Qout);
                String b = String.valueOf(resvr[i].Qin);
                String c = String.valueOf(resvr[i].Qgen);
                writer.write(a+"\t"+b+"\t"+c);
                bw.newLine();
                bw.flush();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    public static void OutN(int Intval,double A,Resvr[] resvr,QZ[] qz,int Nqz){
        String pathname = "src//Nout.txt";
        seek sk = new seek();
        try(FileWriter writer = new FileWriter(pathname);
            BufferedWriter bw = new BufferedWriter(writer)){
            for (int i = 0; i < Intval; i++) {
                double n = A * resvr[i].Qgen * ((resvr[i].Zup + resvr[i + 1].Zup) / 2 - sk.QtoZ(resvr[i].Qout,qz,Nqz));
                if (n > 357.2) n = 357.2;
                String a = String.valueOf(n);
                String b = String.valueOf(resvr[i].profit);
                writer.write(a+"\t"+b);
                bw.newLine();
                bw.flush();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void outPut(int Intval,Resvr[] SimuR){
        String pathname = "src//OutProcess.txt";
        try(FileWriter writer = new FileWriter(pathname);
            BufferedWriter bw = new BufferedWriter(writer)){
            for (int i = 0; i < Intval; i++) {
                String a = String.valueOf(SimuR[i].Zup);
                String b = String.valueOf(SimuR[i].N);
//                String c = String.valueOf(SimuR[i].Qout);
                String d = String.valueOf(SimuR[i].profit);
//                writer.write(a+"\t"+b+"\t"+c+"\t"+d);
                writer.write(a+"\t"+b+"\t"+d);
                bw.newLine();
                bw.flush();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
