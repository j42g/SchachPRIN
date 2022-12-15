import spiel.K;
import spiel.figur.*;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Random r = new Random();
        System.out.println(r.nextInt());


        Bauer wb = new Bauer(K.WEISS);
        System.out.print(wb.toString());
        Bauer bb = new Bauer(K.SCHWARZ);
        System.out.print(bb.toString());
        Dame wd = new Dame(K.WEISS);
        System.out.print(wd.toString());
        Dame bd = new Dame(K.SCHWARZ);
        System.out.print(bd.toString());
        Koenig wk = new Koenig(K.WEISS);
        System.out.print(wk.toString());
        Koenig bk = new Koenig(K.SCHWARZ);
        System.out.print(bk.toString());
        Springer ws = new Springer(K.WEISS);
        System.out.print(ws.toString());
        Springer bs = new Springer(K.SCHWARZ);
        System.out.print(bs.toString());
        Turm wt = new Turm(K.WEISS);
        System.out.print(wt.toString());
        Turm bt = new Turm(K.SCHWARZ);
        System.out.print(bt.toString());




    }
}