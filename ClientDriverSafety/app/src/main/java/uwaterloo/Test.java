package uwaterloo;

import android.content.Context;

import java.math.BigInteger;

public class Test {

    final int lw = 7;
    final int lx = 14;
    final int lr = 300;
    final int la = 300;
    final int lb = 250;
    final int lalpha = 600;
    final int lbeta = 350;
    final int nlength = 35;
    final static int number = 20;
    private Paillier cipher;
    private RSAGroup rsa;
    private BigInteger gg, N, Nsquare, g, h, range_w_1,
            range_w_2,range_x_1,range_x_2, range_r_1,range_r_2,
            range_a_1, range_a_2, range_b_1,range_b_2
            ,v,c,a,b,r_a,v_a,E_x,E_x_a,random
            ,range_alpha_1,range_alpha_2,range_beta_1,range_beta_2
            ,alpha,beta,m, E_cipher,Gamma,U,D,phi_N,exp_tmp,Z;
    private BigInteger[] W, w, C_w, Ga_w, r_w,x;
    private PaillierProof pproof;
    public Test()
    {
        try {
            cipher = new Paillier(Utils.Nlength);
            N = cipher.getN();
            Nsquare = cipher.getNsquare();
            gg = cipher.getG();
            rsa = new RSAGroup(N);
            g = rsa.getG();
            h = rsa.getH();
            pproof = new PaillierProof();
            range_w_1 = BigInteger.valueOf(2).pow(lw).negate();
            range_w_2 = BigInteger.valueOf(2).pow(lw);
            range_x_1 = BigInteger.valueOf(2).pow(lx).negate();
            range_x_2 = BigInteger.valueOf(2).pow(lx);

            range_r_1 = BigInteger.valueOf(2).pow(lr-1);
            range_r_2 = BigInteger.valueOf(2).pow(lr);

            range_a_1 = BigInteger.valueOf(2).pow(la-1);
            range_a_2 = BigInteger.valueOf(2).pow(la);

            range_b_1 = BigInteger.valueOf(2).pow(lb-1);
            range_b_2 = BigInteger.valueOf(2).pow(lb);

            range_alpha_1 = BigInteger.valueOf(2).pow(lalpha-1);
            range_alpha_2 = BigInteger.valueOf(2).pow(lalpha);

            range_beta_1 = BigInteger.valueOf(2).pow(lbeta-1);
            range_beta_2 = BigInteger.valueOf(2).pow(lbeta);

            alpha = Utils.randomRange(range_alpha_1,range_alpha_2);
            beta = Utils.randomRange(range_beta_1,range_beta_2);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void ProcessParams() throws Exception {
        w = new BigInteger[nlength];
        W = new BigInteger[nlength];
        C_w = new BigInteger[nlength];
        Ga_w = new BigInteger[nlength];
        r_w = new BigInteger[nlength];

        for(int i=0;i<nlength;i++) {
            w[i] = Utils.randomRange(range_w_1,range_w_2);
            r_w[i] = Utils.randomZbits(Utils.tau + Utils.Nlength);
            Ga_w[i] = Utils.randomZStarN(Utils.Nlength,N);
            W[i] = cipher.encrypt(w[i].mod(N),Ga_w[i]);
            C_w[i] = g.modPow(w[i], N).multiply(h.modPow(r_w[i], N)).mod(N);
        }
    }

    public ParamsProofParameters ParamProve() {
        return pproof.prove_params(C_w, w, r_w, W, Ga_w, g, h, range_w_1,range_w_2, cipher);
    }
    public boolean ParamVerify(ParamsProofParameters ppp)
    {
        return pproof.verify_params(C_w, W, g, h, range_w_1, range_w_2, cipher, ppp);
    }

    public void driving_data_process(Context context)
    {
        DataProcess.process_data(context);
        x = new BigInteger[nlength-1];
        for(int i=0;i<nlength-1;i++) {
            x[i] = Utils.randomRange(range_x_1, range_x_2);
        }
    }

    public void driving_data_encryption()
    {
        random = Utils.randomRange(range_r_1, range_r_2);
        v = Utils.randomZbits(Utils.tau+Utils.Nlength);
        c = g.modPow(random, N).multiply(h.modPow(v, N)).mod(N);

        a = Utils.randomRange(range_a_1, range_a_2);
        b = Utils.randomRange(range_b_1, range_b_2);

        r_a = random.multiply(a);
        v_a = v.multiply(a);

        E_x = BigInteger.ONE;
        for (int i = 0; i < nlength-1; i++) {
            E_x = E_x.multiply(W[i].modPow(x[i],Nsquare));
        }

        E_x = E_x.multiply(W[nlength-1]).multiply(gg.modPow(random,Nsquare)).mod(Nsquare);

        E_x_a = E_x.modPow(a,Nsquare)
                .multiply(gg.modPow(r_a.negate(),Nsquare))
                .multiply(gg.modPow(b,Nsquare)).mod(Nsquare);
    }


    public ReportProofParameters driving_data_prove() throws Exception {
        return pproof.prove_driving_record(g, h, cipher, c, random, v, a, b, r_a, v_a, E_x, E_x_a, x, W,
                range_a_1, range_a_2, range_b_1, range_b_2, range_r_1, range_r_2, range_x_1, range_x_2);
    }

    public boolean driving_data_verify(ReportProofParameters rpp)
    {
        return pproof.verify_driving_record(g, h, cipher, c, E_x_a, E_x, W, rpp, range_a_1,
                range_a_2, range_b_1, range_b_2, range_r_1, range_r_2, range_x_1, range_x_2);
    }

    public void safety_score_process() throws Exception {
        E_cipher = E_x_a.modPow(alpha,Nsquare).multiply(gg.modPow(beta,Nsquare)).mod(Nsquare);
        m = cipher.decrypt(E_cipher);
        Gamma = Utils.randomZStarN(Utils.Nlength,N);
        U = gg.modPow(m,Nsquare).multiply(Gamma.modPow(N,Nsquare)).mod(Nsquare);
        D = E_cipher.multiply(U.modPow(BigInteger.ONE.negate(),Nsquare)).mod(Nsquare);
        phi_N = cipher.getP().subtract(BigInteger.ONE).multiply(cipher.getQ().subtract(BigInteger.ONE));
        exp_tmp = N.modPow(BigInteger.ONE.negate(), phi_N);
        Z = D.modPow(exp_tmp,N);
    }

    public RecordProofParameters score_prove() throws Exception {
        return pproof.prove_driving_safety(
                g,h,cipher,Z,E_cipher,E_x_a,U,Gamma,alpha,beta,range_alpha_1,
                range_alpha_2,range_beta_1,range_beta_2);
    }

    public boolean score_verify(RecordProofParameters repp)
    {
        return pproof.verify_driving_safety(
                g,h,cipher,E_cipher,E_x_a,U,m,repp,range_alpha_1,
                range_alpha_2,range_beta_1,range_beta_2);
    }

    public void test(Context context) throws Exception {
        long t1=0,t2=0;
        Test test = new Test();
        for(int i=0;i<number;i++) {
            t1 = System.nanoTime();
            test.ProcessParams();
            t2 = System.nanoTime();
        }
        System.out.println("Params Process Time:"+(t2-t1));

        ParamsProofParameters ppp = null;
        for(int i=0;i<number;i++) {
            t1 = System.nanoTime();
            ppp = test.ParamProve();
            t2 = System.nanoTime();
        }
        System.out.println("Params Prove Time:"+(t2-t1));
        boolean res = false;
        for(int i=0;i<number;i++) {
            t1 = System.nanoTime();
            res = test.ParamVerify(ppp);
            t2 = System.nanoTime();
        }
        System.out.println("Params Verify Time:"+(t2-t1));
        System.out.println("Params Verify Correctness:"+res);

        for(int i=0;i<number;i++) {
            t1 = System.nanoTime();
            test.driving_data_process(context);
            t2 = System.nanoTime();
        }
        System.out.println("Driving Data Process Time:"+(t2-t1));

        for(int i=0;i<number;i++) {
            t1 = System.nanoTime();
            test.driving_data_encryption();
            t2 = System.nanoTime();
        }
        System.out.println("Driving Data Encryption Time:"+(t2-t1));

        ReportProofParameters rpp = null;
        for(int i=0;i<number;i++) {
            t1 = System.nanoTime();
            rpp = test.driving_data_prove();
            t2 = System.nanoTime();
        }
        System.out.println("Driving Data Prove Time:"+(t2-t1));

        res = false;
        for(int i=0;i<number;i++) {
            t1 = System.nanoTime();
            res = test.driving_data_verify(rpp);
            t2 = System.nanoTime();
        }
        System.out.println("Driving Data Verify Time:"+(t2-t1));
        System.out.println("Driving Data Verify Correctness:"+res);

        for(int i=0;i<number;i++) {
            t1 = System.nanoTime();
            test.safety_score_process();
            t2 = System.nanoTime();
        }
        System.out.println("Score Safety Process Time:"+(t2-t1));

        RecordProofParameters repp = null;
        for(int i=0;i<number;i++) {
            t1 = System.nanoTime();
            repp = test.score_prove();
            t2 = System.nanoTime();
        }
        System.out.println("Score Safety Prove Time:"+(t2-t1));

        res = false;
        for(int i=0;i<number;i++) {
            t1 = System.nanoTime();
            res = test.score_verify(repp);
            t2 = System.nanoTime();
        }
        System.out.println("Score Safety Verify Time:"+(t2-t1));
        System.out.println("Score Safety Verify Correctness:"+res);
    }

}
