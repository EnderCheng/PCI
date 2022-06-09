package ca.uwaterloo.cheng;

import org.miracl.core.BN254.BIG;
import org.miracl.core.BN254.ECP;
import org.miracl.core.BN254.ROM;
import org.miracl.core.RAND;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class Elgamal {
    BIG p;
    ECP g;
    int bits;
    BIG x;
    ECP h;
    RAND rng;

    public Elgamal()
    {

    }

    public void generateParams()
    {
        p = new BIG(ROM.CURVE_Order);
        bits = p.nbits();
        g = ECP.generator();
        byte[] raw=new byte[100];
        RAND rng=new RAND();
        rng.clean();
        for (int i=0;i<100;i++)
            raw[i]=(byte)i;
        rng.seed(100,raw);
        x = BIG.randomnum(p, rng);
        h = g.mul(x);
    }

    public void generateParams_fixed()
    {
        byte[] g_bytes = Utils.string_to_bytes("AyUjZIJAAAABujRNgAAAAAhhIQAAAAAAE6cAAAAAAAAS");
        byte[] h_bytes = Utils.string_to_bytes("AglyuaRJ7h6M+NYEdpbZ4ABAYuguFAFn9hatIWQEI0cp");
        g = ECP.fromBytes(g_bytes);
        h = ECP.fromBytes(h_bytes);
        x = Utils.BigInteger_to_BIG(new BigInteger("1770542479683057853618647141338097677792213430151504756033849990184638289915"));
        p = Utils.BigInteger_to_BIG(new BigInteger("16798108731015832284940804142231733909759579603404752749028378864165570215949"));
        bits = 254;
        byte[] raw=new byte[100];
        rng=new RAND();
        rng.clean();
        for (int i=0;i<100;i++)
            raw[i]=(byte)i;
        rng.seed(100,raw);
    }

    public void setParams(BIG p, BIG x, ECP g, ECP h, int bits)
    {
        this.p = p;
        this.x = x;
        this.g = g;
        this.h = h;
        this.bits = bits;
        byte[] raw=new byte[100];
        rng=new RAND();
        rng.clean();
        for (int i=0;i<100;i++)
            raw[i]=(byte)i;
        rng.seed(100,raw);
    }

    public ECP[] encrypt(BIG m, BIG r)
    {
        ECP[] ct = new ECP[2];
        ct[0] = g.mul(r);
        ct[1] = g.mul(m);
        ct[1].add(h.mul(r));
//        System.out.println("ct[1]_encrypt:"+ct[1].toRawString());
        return ct;
    }

    public ECP decrypt(ECP[] ct, BIG x)
    {
        ECP s = new ECP();
        s.copy(ct[0]);
        s= s.mul(x);
        ECP tmp = new ECP();
        tmp.copy(ct[1]);
        tmp.sub(s);
        return tmp;
    }


    public void printParams()
    {
        byte[] g_bytes = new byte[33];
        g.toBytes(g_bytes,true);
        System.out.println("g:"+Utils.bytes_to_string(g_bytes));
        byte[] h_bytes = new byte[33];
        h.toBytes(h_bytes,true);
        System.out.println("h:"+Utils.bytes_to_string(h_bytes));
        System.out.println("private key:"+Utils.BIG_to_BigInteger(x).toString());
        System.out.println("bits:"+bits);
        System.out.println("order:"+Utils.BIG_to_BigInteger(p).toString());
    }

    public BIG getX() {
        return x;
    }

    public BIG getP() {
        return p;
    }

    public RAND getRng() {
        return rng;
    }

    public ECP getG() {
        return g;
    }

    public ElgamalProofParameters prove(BIG m, BIG r, ECP[] ct)
    {
        BIG m_r = BIG.randomnum(p, rng);
        BIG r_r = BIG.randomnum(p, rng);
        ECP[] ct_r = new ECP[2];
        ct_r[0] = g.mul(r_r);
        ct_r[1] = g.mul(m_r);
        ct_r[1].add(h.mul(r_r));
        ECP tmp = new ECP();
        tmp.add(ct_r[0]);
        tmp.add(ct_r[1]);
        tmp.add(ct[0]);
        tmp.add(ct[1]);
        tmp.add(g);

        byte[] tmp_bytes = new byte[100];
        tmp.toBytes(tmp_bytes, true);

        BIG e = Utils.BigInteger_to_BIG(Utils.Hash(tmp_bytes));
        e.mod(p);
        BIG ss_m = BIG.modadd(BIG.modmul(e,m,p),m_r,p);
        BIG ss_r = BIG.modadd(BIG.modmul(e,r,p),r_r,p);


        byte[] ct_1 = new byte[33];
        byte[] ct_2 = new byte[33];
        ct_r[0].toBytes(ct_1,true);
        ct_r[1].toBytes(ct_2,true);

        ElgamalProofParameters epp = new ElgamalProofParameters(ct_1,ct_2,Utils.BIG_to_BigInteger(ss_m),Utils.BIG_to_BigInteger(ss_r));
        return epp;
    }

    public boolean verify(ECP[] ct, ElgamalProofParameters epp)
    {
        boolean res = true;
        ECP ct_r_1 = ECP.fromBytes(epp.getCt_1());
//        System.out.println("ct_r_1:"+ct_r_1.toRawString());
        ECP ct_r_2 = ECP.fromBytes(epp.getCt_2());
//        System.out.println("ct_r_2:"+ct_r_2.toRawString());
        BIG ss_m = Utils.BigInteger_to_BIG(epp.getSs_m());
        BIG ss_r = Utils.BigInteger_to_BIG(epp.getSs_r());
//        System.out.println("ss_m"+ss_m.toRawString());
//        System.out.println("ss_r:"+ss_r.toRawString());

        ECP tmp = new ECP();
        tmp.add(ct_r_1);
        tmp.add(ct_r_2);
        tmp.add(ct[0]);
        tmp.add(ct[1]);
        tmp.add(g);
        byte[] tmp_bytes = new byte[100];
        tmp.toBytes(tmp_bytes, true);
        BIG e = Utils.BigInteger_to_BIG(Utils.Hash(tmp_bytes));
        e.mod(p);
//        System.out.println("e verify:"+e.toRawString());
        ECP left = new ECP();
        left.copy(ct_r_1);
        left.add(ct[0].mul(e));
        ECP right = g.mul(ss_r);
        if(left.equals(right) == false)
        {
            System.out.println("fail Elgamal part-1");
            res = false;
        }
        left = new ECP();
        left.copy(ct_r_2);
        left.add(ct[1].mul(e));
        right = h.mul(ss_r);
        right.add(g.mul(ss_m));
        if(left.equals(right) == false)
        {
            System.out.println("fail Elgamal part-2");
            res = false;
        }
        return res;
    }

    public static void main(String[] args) {
        Elgamal elgamal = new Elgamal();
        elgamal.generateParams_fixed();
        ECP g = elgamal.getG();
        BIG p = elgamal.getP();
        RAND rng = elgamal.getRng();
        BIG x = elgamal.getX();
        BIG m = Utils.BigInteger_to_BIG(new BigInteger("123456"));
        BIG r = BIG.randomnum(p, rng);
        ECP[] ct = elgamal.encrypt(m, r);
        ECP d_m = elgamal.decrypt(ct, x);
        System.out.println(g.mul(m).equals(d_m));
        ElgamalProofParameters epp = elgamal.prove(m,r,ct);
        boolean res = elgamal.verify(ct,epp);
        System.out.println(res);
        epp = elgamal.prove(m,r,ct);
        res = elgamal.verify(ct,epp);
        System.out.println(res);


        AES aes = new AES(Utils.getKey(g.mul(m)),Utils.getIV());
        byte[] text = Utils.fillBlock("sdafaferedsfgfgdgdfgwrwe").getBytes(StandardCharsets.UTF_8);
        byte[] cipher = aes.CBC_encrypt(text);
        byte[] de_text = aes.CBC_decrypt(cipher);
        System.out.println(new String(de_text,StandardCharsets.UTF_8));
    }

}
