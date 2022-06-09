package ca.uwaterloo;

import java.math.BigInteger;

public class PaillierProof {

    public PaillierProof()
    {

    }

    public ReportProofParameters prove_driving_record(BigInteger g, BigInteger h, Paillier cipher, BigInteger c,
                                                      BigInteger r, BigInteger v, BigInteger a, BigInteger b,
                                                      BigInteger r_a, BigInteger v_a,BigInteger E, BigInteger E_a,
                                                      BigInteger[] x, BigInteger[] W,
                                                      BigInteger range_a_1, BigInteger range_a_2, BigInteger range_b_1, BigInteger range_b_2,
                                                      BigInteger range_r_1, BigInteger range_r_2, BigInteger range_x_1, BigInteger range_x_2) throws Exception {
        int len =  x.length;
        BigInteger[]  xx = new BigInteger[len];
        for (int i = 0; i < len; i++) {
            xx[i] = Utils.randomZbits(Utils.tau+Utils.Nlength);
        }
        BigInteger gg = cipher.getG();
        BigInteger N = cipher.getN();
        BigInteger Nsquare = cipher.getN();
        BigInteger bb = Utils.randomZbits(Utils.tau+Utils.Nlength);
        BigInteger vv = Utils.randomZbits(Utils.tau+Utils.Nlength);
        BigInteger rr = Utils.randomZbits(Utils.tau+Utils.Nlength);
        BigInteger vv_a = Utils.randomZbits(Utils.tau+Utils.Nlength);
        BigInteger rr_a = Utils.randomZbits(Utils.tau+Utils.Nlength);
        BigInteger aa = Utils.randomZbits(Utils.tau+Utils.Nlength);
        BigInteger c_r = g.modPow(rr,N).multiply(h.modPow(vv,N)).mod(N);
        BigInteger com = g.modPow(rr_a,N).multiply(h.modPow(vv_a,N)).multiply(c.modPow(aa.negate(),N)).mod(N);
        BigInteger E_r = BigInteger.ONE;
        for (int i = 0; i < len; i++) {
            E_r = E_r.multiply(W[i].modPow(xx[i],Nsquare));
        }
        E_r = E_r.multiply(gg.modPow(rr,Nsquare)).mod(Nsquare);
        BigInteger E_r_a = E.modPow(aa,Nsquare)
                .multiply(gg.modPow(rr_a.negate(),Nsquare))
                .multiply(gg.modPow(bb,Nsquare)).mod(Nsquare);
        BigInteger e = Utils.Hash((E_r.toString()+E_r_a.toString()+com.toString()+c_r.toString()
                +c.toString()+W[0].toString()+E_a.toString()+E.toString()+N.toString()+g.toString()).getBytes());
        BigInteger[] ss_xx = new BigInteger[len];
        for (int i = 0; i < len; i++) {
            ss_xx[i] = e.multiply(x[i]).add(xx[i]);
        }
        BigInteger ss_rr = e.multiply(r).add(rr);
        BigInteger ss_vv = e.multiply(v).add(vv);
        BigInteger ss_rr_a = e.multiply(r_a).add(rr_a);
        BigInteger ss_vv_a = e.multiply(v_a).add(vv_a);
        BigInteger ss_aa = e.multiply(a).add(aa);
        BigInteger ss_bb = e.multiply(b).add(bb);

        BigInteger a_r = Utils.randomZbits(Utils.tau+Utils.Nlength);
        BigInteger b_r = Utils.randomZbits(Utils.tau+Utils.Nlength);
        BigInteger com_a = g.modPow(a,N).multiply(h.modPow(a_r,N)).mod(N);
        BigInteger com_b = g.modPow(b,N).multiply(h.modPow(b_r,N)).mod(N);

        BigInteger a_tmp = BigInteger.valueOf(4).multiply(range_a_2.subtract(a)).multiply(a.subtract(range_a_1)).add(BigInteger.ONE);
        BigInteger b_tmp = BigInteger.valueOf(4).multiply(range_b_2.subtract(b)).multiply(b.subtract(range_b_1)).add(BigInteger.ONE);
        BigInteger r_tmp = BigInteger.valueOf(4).multiply(range_r_2.subtract(r)).multiply(r.subtract(range_r_1)).add(BigInteger.ONE);
        BigInteger[] x_a = Utils.three_squares(a_tmp);
        BigInteger[] x_b = Utils.three_squares(b_tmp);
        BigInteger[] x_r = Utils.three_squares(r_tmp);

        RangeProof rangeproof = new RangeProof();
        RangeProofParameters p_a = rangeproof.prove(g,h,N,a,a_r,com_a,x_a[0],x_a[1],x_a[2],range_a_1,range_a_2,e);
        RangeProofParameters p_b = rangeproof.prove(g,h,N,b,b_r,com_b,x_b[0],x_b[1],x_b[2],range_b_1,range_b_2,e);
        RangeProofParameters p_r = rangeproof.prove(g,h,N,r,v,c,x_r[0],x_r[1],x_r[2],range_r_1,range_r_2,e);

        BigInteger[] com_x = new BigInteger[len];
        BigInteger[] r_x = new BigInteger[len];
        BigInteger[] x_1 = new BigInteger[len];
        BigInteger[] x_2 = new BigInteger[len];
        BigInteger[] x_3 = new BigInteger[len];

        for(int i=0;i<len;i++) {
            BigInteger tmp = BigInteger.valueOf(4).multiply(range_x_2.subtract(x[i])).multiply(x[i].subtract(range_x_1)).add(BigInteger.ONE);
            BigInteger[] x_tmp = Utils.three_squares(tmp);
            x_1[i] = x_tmp[0].abs();
            x_2[i] = x_tmp[1].abs();
            x_3[i] = x_tmp[2].abs();
            r_x[i] = Utils.randomZbits(Utils.tau+Utils.Nlength);
            com_x[i] = g.modPow(x[i],N).multiply(h.modPow(r_x[i],N)).mod(N);
        }
        RangeProofParameters[] rps = rangeproof.prove_range(g,h,N,x,r_x,com_x,x_1,x_2,x_3,range_x_1,range_x_2,e);
        ReportProofParameters  rpp = new ReportProofParameters(c_r,com,E_r,E_r_a,ss_xx,ss_rr,ss_vv
                ,ss_rr_a,ss_vv_a,ss_aa,ss_bb,p_a,p_b,p_r,rps,com_a,com_b,com_x);
        return rpp;
    }

    public boolean verify_driving_record(BigInteger g, BigInteger h, Paillier cipher, BigInteger c,
                                         BigInteger E_a, BigInteger E, BigInteger[] W, ReportProofParameters rpp,
                                         BigInteger range_a_1, BigInteger range_a_2, BigInteger range_b_1, BigInteger range_b_2,
                                         BigInteger range_r_1, BigInteger range_r_2, BigInteger range_x_1, BigInteger range_x_2)
    {
        boolean res = true;
        int len = W.length;
        BigInteger E_r = rpp.getE_r();
        BigInteger E_r_a = rpp.getE_r_a();
        BigInteger com = rpp.getCom();
        BigInteger c_r = rpp.getC_r();
        BigInteger gg = cipher.getG();
        BigInteger N = cipher.getN();
        BigInteger Nsquare = cipher.getN();
        BigInteger e = Utils.Hash((E_r.toString()+E_r_a.toString()+com.toString()+c_r.toString()
                +c.toString()+W[0].toString()+E_a.toString()+E.toString()+N.toString()+g.toString()).getBytes());

        BigInteger ss_rr = rpp.getSs_rr();
        BigInteger ss_vv = rpp.getSs_vv();
        BigInteger left = c_r.multiply(c.modPow(e,N)).mod(N);
        BigInteger right = g.modPow(ss_rr,N).multiply(h.modPow(ss_vv,N)).mod(N);
        if(left.compareTo(right) !=0)
        {
            System.out.println("false commitment c=g^rh^v");
            res = false;
        }
        BigInteger ss_rr_a = rpp.getSs_rr_a();
        BigInteger ss_vv_a = rpp.getSs_vv_a();
        BigInteger ss_aa = rpp.getSs_aa();
        left = com;
        right = g.modPow(ss_rr_a,N).multiply(h.modPow(ss_vv_a,N)).multiply(c.modPow(ss_aa.negate(),N)).mod(N);
        if(left.compareTo(right) !=0)
        {
            System.out.println("false commitment 1 = g^r'h^v'c^-a");
            res = false;
        }

        BigInteger[] ss_xx = rpp.getSs_xx();
        BigInteger E_tmp = E.multiply(W[len-1].modPow(BigInteger.ONE.negate(),Nsquare));
        left = E_r.multiply(E_tmp.modPow(e,Nsquare)).mod(Nsquare);
        right = BigInteger.ONE;
        for (int i = 0; i < len-1; i++) {
            right = right.multiply(W[i].modPow(ss_xx[i],Nsquare));
        }
        right = right.multiply(gg.modPow(ss_rr,Nsquare)).mod(Nsquare);
        if(left.compareTo(right) !=0)
        {
            System.out.println("false encryption E = E_1^x_1 ... E_n^x_n E_n+1 (1+N)^r");
            res = false;
        }
        BigInteger ss_bb = rpp.getSs_bb();
        left = E_r_a.multiply(E_a.modPow(e,Nsquare)).mod(Nsquare);
        right = E.modPow(ss_aa,Nsquare)
            .multiply(gg.modPow(ss_rr_a.negate(),Nsquare))
            .multiply(gg.modPow(ss_bb,Nsquare)).mod(Nsquare);
        if(left.compareTo(right) !=0)
        {
            System.out.println("false encryption E_r_a = E_r^a (1+N)^-r (1+N)^b");
            res = false;
        }
        RangeProofParameters p_a = rpp.getP_a();
        RangeProofParameters p_b = rpp.getP_b();
        RangeProofParameters p_r = rpp.getP_r();
        RangeProof rangeproof = new RangeProof();
        RangeProofParameters[] rps = rpp.getRps();
        boolean tmp_res = rangeproof.verify_range(g,h,N,range_x_1,range_x_2,rps,e);
        if(tmp_res != true){
            System.out.println("false_range_proof x");
            res = false;
        }
        tmp_res = rangeproof.verify(g,h,N,range_a_1,range_a_2,p_a,e);
        if(tmp_res != true){
            System.out.println("false_range_proof a");
            res = false;
        }

        tmp_res = rangeproof.verify(g,h,N,range_b_1,range_b_2,p_b,e);
        if(tmp_res != true){
            System.out.println("false_range_proof b");
            res = false;
        }

        tmp_res = rangeproof.verify(g,h,N,range_r_1,range_r_2,p_r,e);
        if(tmp_res != true){
            System.out.println("false_range_proof r");
            res = false;
        }

        return res;
    }

    public ParamsProofParameters prove_params(BigInteger[] C, BigInteger[] x, BigInteger[] r,
                                    BigInteger[] E, BigInteger[] Ga,
                                    BigInteger g, BigInteger h,
                                    BigInteger a, BigInteger b, Paillier paillier)
    {
        int len = C.length;
        BigInteger gg = paillier.getG();
        BigInteger N = paillier.getN();
        BigInteger Nsquare = paillier.getNsquare();
        BigInteger[] xx = new BigInteger[len];
        BigInteger[] rr = new BigInteger[len];
        BigInteger[] GaGa = new BigInteger[len];
        BigInteger[] CC = new BigInteger[len];
        BigInteger[] EE = new BigInteger[len];
        BigInteger[] ss_x = new BigInteger[len];
        BigInteger[] ss_r = new BigInteger[len];
        BigInteger[] ss_Ga = new BigInteger[len];
        BigInteger[] x_1 = new BigInteger[len];
        BigInteger[] x_2 = new BigInteger[len];
        BigInteger[] x_3 = new BigInteger[len];
        for(int i=0;i<len;i++)
        {
            BigInteger tmp = BigInteger.valueOf(4).multiply(b.subtract(x[i])).multiply(x[i].subtract(a)).add(BigInteger.ONE);
            BigInteger[] x_tmp = new BigInteger[3];
            try {
                x_tmp = Utils.three_squares(tmp);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            x_1[i] = x_tmp[0].abs();
            x_2[i] = x_tmp[1].abs();
            x_3[i] = x_tmp[2].abs();

            xx[i] = Utils.randomZbits(Utils.tau+Utils.Nlength);
            rr[i] = Utils.randomZbits(Utils.tau+Utils.Nlength);
            GaGa[i] = Utils.randomZStarN(Utils.Nlength,N);
            CC[i] = g.modPow(xx[i],N).multiply(h.modPow(rr[i],N)).mod(N);
            EE[i] = gg.modPow(xx[i],Nsquare).multiply(GaGa[i].modPow(N,Nsquare)).mod(Nsquare);
        }
        BigInteger e = Utils.Hash((CC[0].toString()+EE[0].toString()+C[0].toString()+E[0].toString()+N.toString()+g.toString()).getBytes());
        for(int i=0;i<len;i++)
        {
            ss_x[i] = e.multiply(x[i]).add(xx[i]);
            ss_r[i] = e.multiply(r[i]).add(rr[i]);
            ss_Ga[i] = GaGa[i].multiply(Ga[i].modPow(e,N)).mod(N);
        }
        RangeProof rangeproof = new RangeProof();
        RangeProofParameters[] rps = rangeproof.prove_range(g,h,N,x,r,C,x_1,x_2,x_3,a,b,e);
        ParamsProofParameters ppp =new ParamsProofParameters(CC,EE,ss_x,ss_r,ss_Ga,rps);
        return ppp;
    }

    public boolean verify_params(BigInteger[] C, BigInteger[] E, BigInteger g, BigInteger h,
                                     BigInteger a, BigInteger b, Paillier paillier, ParamsProofParameters ppp)
    {
        int len = C.length;
        boolean res = true;
        RangeProof rangeproof = new RangeProof();
        BigInteger gg = paillier.getG();
        BigInteger N = paillier.getN();
        BigInteger Nsquare = paillier.getNsquare();
        BigInteger[] EE = ppp.getEE();
        BigInteger[] CC = ppp.getCC();
        BigInteger[] ss_x = ppp.getSs_x();
        BigInteger[] ss_r = ppp.getSs_r();
        BigInteger[] ss_Ga = ppp.getSs_Ga();
        RangeProofParameters[] rps = ppp.getRps();
        BigInteger e = Utils.Hash((CC[0].toString()+EE[0].toString()+C[0].toString()+E[0].toString()+N.toString()+g.toString()).getBytes());
        for(int i=0;i<len;i++)
        {
            BigInteger tmp_1 = EE[i].multiply(E[i].modPow(e,Nsquare)).mod(Nsquare);
            BigInteger tmp_2 =  gg.modPow(ss_x[i],Nsquare).multiply(ss_Ga[i].modPow(N,Nsquare)).mod(Nsquare);
            if(tmp_1.compareTo(tmp_2) != 0) {
                System.out.println("false_encryption");
                res = false;
            }
            tmp_1 = CC[i].multiply(C[i].modPow(e,N)).mod(N);
            tmp_2 = g.modPow(ss_x[i],N).multiply(h.modPow(ss_r[i],N)).mod(N);
            if(tmp_1.compareTo(tmp_2) != 0) {
                System.out.println("false_commit");
                res = false;
            }
        }
        boolean tmp_res = rangeproof.verify_range(g,h,N,a,b,rps,e);
        if(tmp_res != true){
            System.out.println("false_range_proof");
            res = false;
        }
        return res;
    }

    public RecordProofParameters prove_driving_safety(BigInteger g, BigInteger h, Paillier cipher,
                                     BigInteger Z, BigInteger E, BigInteger B,
                                     BigInteger U, BigInteger Ga,
                                     BigInteger a, BigInteger b,
                                     BigInteger range_a_1, BigInteger range_a_2,
                                     BigInteger range_b_1, BigInteger range_b_2) throws Exception {
        BigInteger gg = cipher.getG();
        BigInteger N = cipher.getN();
        BigInteger Nsquare = cipher.getNsquare();
        BigInteger D = E.multiply(U.modPow(BigInteger.ONE.negate(),Nsquare)).mod(Nsquare);
        BigInteger Z_r = Utils.randomZStarN(Utils.Nlength,N);
        BigInteger a_r = Utils.randomZbits(Utils.tau+Utils.Nlength);
        BigInteger b_r = Utils.randomZbits(Utils.tau+Utils.Nlength);
        //BigInteger U_tmp = U.multiply(gg.modPow(m.negate(),Nsquare));
        BigInteger Ga_r = Utils.randomZStarN(Utils.Nlength,N);
        BigInteger com_a = g.modPow(a,N).multiply(h.modPow(a_r,N)).mod(N);
        BigInteger com_b = g.modPow(b,N).multiply(h.modPow(b_r,N)).mod(N);

        BigInteger a_tmp = BigInteger.valueOf(4).multiply(range_a_2.subtract(a)).multiply(a.subtract(range_a_1)).add(BigInteger.ONE);
        BigInteger b_tmp = BigInteger.valueOf(4).multiply(range_b_2.subtract(b)).multiply(b.subtract(range_b_1)).add(BigInteger.ONE);
        BigInteger[] x_a = Utils.three_squares(a_tmp);
        BigInteger[] x_b = Utils.three_squares(b_tmp);

        BigInteger D_r = Z_r.modPow(N,Nsquare);
        BigInteger E_r = B.modPow(a_r,Nsquare).multiply(gg.modPow(b_r,Nsquare)).mod(Nsquare);
        BigInteger U_tmp_r = Ga_r.modPow(N,Nsquare);

        BigInteger e = Utils.Hash((D.toString()+E.toString()+U.toString()+D_r.toString()
                +E_r.toString()+U_tmp_r.toString()+N.toString()+g.toString()).getBytes());

        RangeProof rangeproof = new RangeProof();
        RangeProofParameters p_a = rangeproof.prove(g,h,N,a,a_r,com_a,x_a[0],x_a[1],x_a[2],range_a_1,range_a_2,e);
        RangeProofParameters p_b = rangeproof.prove(g,h,N,b,b_r,com_b,x_b[0],x_b[1],x_b[2],range_b_1,range_b_2,e);

        BigInteger ZZ_r = Z_r.multiply(Z.modPow(e,N)).mod(N);
        BigInteger ZGa_r = Ga_r.multiply(Ga.modPow(e,N)).mod(N);
        BigInteger ss_a = e.multiply(a).add(a_r);
        BigInteger ss_b = e.multiply(b).add(b_r);

        RecordProofParameters rpp = new RecordProofParameters(ss_a,ss_b,ZZ_r,
                ZGa_r,D_r,E_r,U_tmp_r,p_a,p_b);
        return rpp;
    }

    public boolean verify_driving_safety(BigInteger g, BigInteger h, Paillier cipher,
                                                      BigInteger E, BigInteger B,
                                                      BigInteger U, BigInteger m,
                                                      RecordProofParameters rpp,
                                                      BigInteger range_a_1, BigInteger range_a_2,
                                                      BigInteger range_b_1, BigInteger range_b_2)
    {
        boolean res = true;
        BigInteger N = cipher.getN();
        BigInteger Nsquare = cipher.getNsquare();
        BigInteger gg = cipher.getG();
        BigInteger D = E.multiply(U.modPow(BigInteger.ONE.negate(),Nsquare)).mod(Nsquare);
        BigInteger D_r = rpp.getD_r();
        BigInteger E_r = rpp.getE_r();
        BigInteger U_tmp_r = rpp.getU_tmp_r();
        BigInteger e = Utils.Hash((D.toString()+E.toString()+U.toString()+D_r.toString()
                +E_r.toString()+U_tmp_r.toString()+N.toString()+g.toString()).getBytes());
        BigInteger ZZ_r = rpp.getZZ_r();
        BigInteger left = D_r.multiply(D.modPow(e,Nsquare)).mod(Nsquare);
        BigInteger right = ZZ_r.modPow(N,Nsquare);
        if(left.compareTo(right) !=0)
        {
            System.out.println("false D=Z^N");
            res = false;
        }
        BigInteger ss_a = rpp.ss_a;
        BigInteger ss_b = rpp.ss_b;
        left = E_r.multiply(E.modPow(e,Nsquare)).mod(Nsquare);
        right = B.modPow(ss_a,Nsquare).multiply(gg.modPow(ss_b,Nsquare)).mod(Nsquare);
        if(left.compareTo(right) !=0)
        {
            System.out.println("false E = B^alpha (1+N)^beta");
            res = false;
        }
        BigInteger ZGa_r = rpp.getZGa_r();
        BigInteger U_tmp = U.multiply(gg.modPow(m.negate(),Nsquare)).mod(Nsquare);
        left = U_tmp_r.multiply(U_tmp.modPow(e,Nsquare)).mod(Nsquare);
        right = ZGa_r.modPow(N,Nsquare);

        if(left.compareTo(right) !=0)
        {
            System.out.println("false E = B^alpha (1+N)^beta");
            res = false;
        }

        RangeProofParameters p_a = rpp.getP_a();
        RangeProofParameters p_b = rpp.getP_b();
        RangeProof rangeproof = new RangeProof();
        boolean tmp_res = rangeproof.verify(g,h,N,range_a_1,range_a_2,p_a,e);

        if(tmp_res != true){
            System.out.println("false_range_proof alpha");
            res = false;
        }

        tmp_res = rangeproof.verify(g,h,N,range_b_1,range_b_2,p_b,e);
        if(tmp_res != true){
            System.out.println("false_range_proof beta");
            res = false;
        }

        return res;
    }

}
