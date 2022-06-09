package ca.uwaterloo;

import org.apache.commons.lang3.SerializationUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

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
//        w = SerializationUtils.deserialize(Base64.getDecoder().decode("rO0ABXVyABdbTGphdmEubWF0aC5CaWdJbnRlZ2VyOw5820bgOmDGAgAAeHAAAAAjc3IAFGphdmEubWF0aC5CaWdJbnRlZ2VyjPyfH6k7+x0DAAZJAAhiaXRDb3VudEkACWJpdExlbmd0aEkAE2ZpcnN0Tm9uemVyb0J5dGVOdW1JAAxsb3dlc3RTZXRCaXRJAAZzaWdudW1bAAltYWduaXR1ZGV0AAJbQnhyABBqYXZhLmxhbmcuTnVtYmVyhqyVHQuU4IsCAAB4cP///////////////v////7/////dXIAAltCrPMX+AYIVOACAAB4cAAAAAEJeHNxAH4AAv///////////////v////4AAAABdXEAfgAGAAAAAR14c3EAfgAC///////////////+/////v////91cQB+AAYAAAABbnhzcQB+AAL///////////////7////+AAAAAXVxAH4ABgAAAAFTeHNxAH4AAv///////////////v////7/////dXEAfgAGAAAAAWB4c3EAfgAC///////////////+/////v////91cQB+AAYAAAABSnhzcQB+AAL///////////////7////+AAAAAXVxAH4ABgAAAAFPeHNxAH4AAv///////////////v////4AAAABdXEAfgAGAAAAAQx4c3EAfgAC///////////////+/////gAAAAF1cQB+AAYAAAABXHhzcQB+AAL///////////////7////+/////3VxAH4ABgAAAAEHeHNxAH4AAv///////////////v////4AAAABdXEAfgAGAAAAARt4c3EAfgAC///////////////+/////gAAAAF1cQB+AAYAAAABenhzcQB+AAL///////////////7////+/////3VxAH4ABgAAAAEreHNxAH4AAv///////////////v////7/////dXEAfgAGAAAAATt4c3EAfgAC///////////////+/////v////91cQB+AAYAAAABfHhzcQB+AAL///////////////7////+/////3VxAH4ABgAAAAEFeHNxAH4AAv///////////////v////4AAAABdXEAfgAGAAAAAUd4c3EAfgAC///////////////+/////gAAAAF1cQB+AAYAAAABEHhzcQB+AAL///////////////7////+/////3VxAH4ABgAAAAEYeHNxAH4AAv///////////////v////4AAAABdXEAfgAGAAAAAQh4c3EAfgAC///////////////+/////gAAAAF1cQB+AAYAAAABX3hzcQB+AAL///////////////7////+/////3VxAH4ABgAAAAFfeHNxAH4AAv///////////////v////7/////dXEAfgAGAAAAASp4c3EAfgAC///////////////+/////v////91cQB+AAYAAAABWnhzcQB+AAL///////////////7////+/////3VxAH4ABgAAAAEVeHNxAH4AAv///////////////v////7/////dXEAfgAGAAAAAWd4c3EAfgAC///////////////+/////v////91cQB+AAYAAAABFnhzcQB+AAL///////////////7////+AAAAAXVxAH4ABgAAAAE8eHNxAH4AAv///////////////v////7/////dXEAfgAGAAAAAVB4c3EAfgAC///////////////+/////gAAAAF1cQB+AAYAAAABXnhzcQB+AAL///////////////7////+AAAAAXVxAH4ABgAAAAF2eHNxAH4AAv///////////////v////4AAAABdXEAfgAGAAAAAV54cQB+ACxxAH4AGHNxAH4AAv///////////////v////4AAAABdXEAfgAGAAAAASt4"));
//        W = SerializationUtils.deserialize(Base64.getDecoder().decode("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"));
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

//        String str_w = Base64.getEncoder().encodeToString(SerializationUtils.serialize(w));
//        System.out.println(str_w);
//        w = SerializationUtils.deserialize(Base64.getDecoder().decode(str_w));
//
//
//        String str_W = Base64.getEncoder().encodeToString(SerializationUtils.serialize(W));
//        System.out.println(str_W);
//        W = SerializationUtils.deserialize(Base64.getDecoder().decode(str_W));

    }

    public ParamsProofParameters ParamProve() {
        return pproof.prove_params(C_w, w, r_w, W, Ga_w, g, h, range_w_1,range_w_2, cipher);
    }
    public boolean ParamVerify(ParamsProofParameters ppp)
    {
        return pproof.verify_params(C_w, W, g, h, range_w_1, range_w_2, cipher, ppp);
    }

    public void driving_data_process()
    {
        DataProcess.process_data();
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

    public static void main(String[] args) throws Exception {
        double prec = Math.pow(10,6);
        long t1=0,t2=0;
        Test test = new Test();
        List<Double> params_prcess = new ArrayList<>();
        List<Double> params_prove = new ArrayList<>();
        List<Double> params_verify = new ArrayList<>();
        List<Double> driving_process = new ArrayList<>();
        List<Double> driving_encrypt = new ArrayList<>();
        List<Double> driving_prove = new ArrayList<>();
        List<Double> driving_verify = new ArrayList<>();
        List<Double> record_process = new ArrayList<>();
        List<Double> record_prove = new ArrayList<>();
        List<Double> record_verify = new ArrayList<>();
        for(int i=0;i<number;i++) {
            t1 = System.nanoTime();
            test.ProcessParams();
            t2 = System.nanoTime();
            params_prcess.add((t2-t1)/prec);
        }
        System.out.println("Params Process Time:"+(t2-t1));


        ParamsProofParameters ppp = null;
        for(int i=0;i<number;i++) {
            t1 = System.nanoTime();
            ppp = test.ParamProve();
            t2 = System.nanoTime();
            params_prove.add((t2-t1)/prec);
        }
        System.out.println("Params Prove Time:"+(t2-t1));


        boolean res = false;
        for(int i=0;i<number;i++) {
            t1 = System.nanoTime();
            res = test.ParamVerify(ppp);
            t2 = System.nanoTime();
            params_verify.add((t2-t1)/prec);
        }
        System.out.println("Params Verify Time:"+(t2-t1));
        System.out.println("Params Verify Correctness:"+res);


        for(int i=0;i<number;i++) {
            t1 = System.nanoTime();
            test.driving_data_process();
            t2 = System.nanoTime();
            driving_process.add((t2-t1)/prec);
        }
        System.out.println("Driving Data Process Time:"+(t2-t1));


        for(int i=0;i<number;i++) {
            t1 = System.nanoTime();
            test.driving_data_encryption();
            t2 = System.nanoTime();
            driving_encrypt.add((t2-t1)/prec);
        }
        System.out.println("Driving Data Encryption Time:"+(t2-t1));


        ReportProofParameters rpp = null;
        for(int i=0;i<number;i++) {
            t1 = System.nanoTime();
            rpp = test.driving_data_prove();
            t2 = System.nanoTime();
            driving_prove.add((t2-t1)/prec);
        }
        System.out.println("Driving Data Prove Time:"+(t2-t1));


        res = false;
        for(int i=0;i<number;i++) {
            t1 = System.nanoTime();
            res = test.driving_data_verify(rpp);
            t2 = System.nanoTime();
            driving_verify.add((t2-t1)/prec);
        }
        System.out.println("Driving Data Verify Time:"+(t2-t1));
        System.out.println("Driving Data Verify Correctness:"+res);


        for(int i=0;i<number;i++) {
            t1 = System.nanoTime();
            test.safety_score_process();
            t2 = System.nanoTime();
            record_process.add((t2-t1)/prec);
        }
        System.out.println("Score Safety Process Time:"+(t2-t1));


        RecordProofParameters repp = null;
        for(int i=0;i<number;i++) {
            t1 = System.nanoTime();
            repp = test.score_prove();
            t2 = System.nanoTime();
            record_prove.add((t2-t1)/prec);
        }
        System.out.println("Score Safety Prove Time:"+(t2-t1));


        res = false;
        for(int i=0;i<number;i++) {
            t1 = System.nanoTime();
            res = test.score_verify(repp);
            t2 = System.nanoTime();
            record_verify.add((t2-t1)/prec);
        }
        System.out.println("Score Safety Verify Time:"+(t2-t1));
        System.out.println("Score Safety Verify Correctness:"+res);


        System.out.println("params_process:"+ Collections.min(params_prcess)
                +","+Utils.calculateAverage(params_prcess)+","+Collections.max(params_prcess));
        System.out.println("params_prove:"+ Collections.min(params_prove)
                +","+Utils.calculateAverage(params_prove)+","+Collections.max(params_prove));
        System.out.println("params_verify:"+ Collections.min(params_verify)
                +","+Utils.calculateAverage(params_verify)+","+Collections.max(params_verify));

        System.out.println("driving_process:"+ Collections.min(driving_process)
                +","+Utils.calculateAverage(driving_process)+","+Collections.max(driving_process));
        System.out.println("driving_encrypt:"+ Collections.min(driving_encrypt)
                +","+Utils.calculateAverage(driving_encrypt)+","+Collections.max(driving_encrypt));
        System.out.println("driving_prove:"+ Collections.min(driving_prove)
                +","+Utils.calculateAverage(driving_prove)+","+Collections.max(driving_prove));
        System.out.println("driving_verify:"+ Collections.min(driving_verify)
                +","+Utils.calculateAverage(driving_verify)+","+Collections.max(driving_verify));

        System.out.println("record_process:"+ Collections.min(record_process)
                +","+Utils.calculateAverage(record_process)+","+Collections.max(record_process));
        System.out.println("record_prove:"+ Collections.min(record_prove)
                +","+Utils.calculateAverage(record_prove)+","+Collections.max(record_prove));
        System.out.println("record_verify:"+ Collections.min(record_verify)
                +","+Utils.calculateAverage(record_verify)+","+Collections.max(record_verify));


    }



}
