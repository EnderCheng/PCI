package uwaterloo;

import java.math.BigInteger;

public class RangeProof {

    public RangeProof()
    {

    }

    public RangeProofParameters[] prove_range(BigInteger g, BigInteger h, BigInteger N,
                                              BigInteger[] x, BigInteger[] r, BigInteger[] c,
                                              BigInteger[] x_1, BigInteger[] x_2, BigInteger[] x_3,
                                              BigInteger a, BigInteger b, BigInteger e)
    {
        int len = x.length;
        RangeProofParameters[] rps = new RangeProofParameters[len];
        for(int i=0;i<len;i++)
        {
            rps[i] = prove(g,h,N,x[i],r[i],c[i],x_1[i],x_2[i],x_3[i],a,b,e);
        }
        return rps;
    }

    public boolean verify_range(BigInteger g, BigInteger h, BigInteger N,
                                BigInteger a, BigInteger b,
                                RangeProofParameters[] proofs, BigInteger e)
    {
        boolean res = true;
        int len = proofs.length;
        for(int i=0;i<len;i++)
        {
            if(verify(g,h,N,a,b,proofs[i],e) == false)
                res =false;
        }
        return res;
    }

    public RangeProofParameters prove(BigInteger g, BigInteger h, BigInteger N,
                                      BigInteger x, BigInteger r, BigInteger c,
                                      BigInteger x_1, BigInteger x_2, BigInteger x_3,
                                      BigInteger a, BigInteger b, BigInteger e)
    {
        BigInteger c_a = c.multiply(g.modPow(a.negate(),N)).pow(4).mod(N);
        BigInteger r_1 = Utils.randomZN(Utils.Nlength-1,N);
        BigInteger r_2 = Utils.randomZN(Utils.Nlength-1,N);
        BigInteger r_3 = Utils.randomZN(Utils.Nlength-1,N);
        BigInteger c_1 = g.modPow(x_1,N).multiply(h.modPow(r_1,N)).mod(N);
        BigInteger c_2 = g.modPow(x_2,N).multiply(h.modPow(r_2,N)).mod(N);
        BigInteger c_3 = g.modPow(x_3,N).multiply(h.modPow(r_3,N)).mod(N);
        BigInteger x_0 = b.subtract(x);
        BigInteger r_0 = r.negate(); // error in paper, should be r_0 = -r;
        BigInteger m_0 = Utils.randomZbits(Utils.tau);
        BigInteger m_1 = Utils.randomZbits(Utils.tau);
        BigInteger m_2 = Utils.randomZbits(Utils.tau);
        BigInteger m_3 = Utils.randomZbits(Utils.tau);
        BigInteger s_0 = Utils.randomZbits(Utils.tau+Utils.Nlength);
        BigInteger s_1 = Utils.randomZbits(Utils.tau+Utils.Nlength);
        BigInteger s_2 = Utils.randomZbits(Utils.tau+Utils.Nlength);
        BigInteger s_3 = Utils.randomZbits(Utils.tau+Utils.Nlength);
        BigInteger sigma = Utils.randomZbits(Utils.tau+Utils.Nlength);
        BigInteger cc_0 = g.modPow(m_0,N).multiply(h.modPow(s_0,N)).mod(N);
        BigInteger cc_1 = g.modPow(m_1,N).multiply(h.modPow(s_1,N)).mod(N);
        BigInteger cc_2 = g.modPow(m_2,N).multiply(h.modPow(s_2,N)).mod(N);
        BigInteger cc_3 = g.modPow(m_3,N).multiply(h.modPow(s_3,N)).mod(N);
        BigInteger cc = h.modPow(sigma,N).multiply(c_a.modPow(m_0,N))
                .multiply(c_1.modPow(m_1.negate(),N))
                .multiply(c_2.modPow(m_2.negate(),N))
                .multiply(c_3.modPow(m_3.negate(),N))
                .mod(N);
//        System.out.println("cc_0:"+cc_0);
//        System.out.println("cc_1:"+cc_1);
//        System.out.println("cc_2:"+cc_2);
//        System.out.println("cc_3:"+cc_3);
//        System.out.println("cc:"+cc);
        BigInteger z_0 = e.multiply(x_0).add(m_0);
        BigInteger z_1 = e.multiply(x_1).add(m_1);
        BigInteger z_2 = e.multiply(x_2).add(m_2);
        BigInteger z_3 = e.multiply(x_3).add(m_3);

        BigInteger t_0 = e.multiply(r_0).add(s_0);
        BigInteger t_1 = e.multiply(r_1).add(s_1);
        BigInteger t_2 = e.multiply(r_2).add(s_2);
        BigInteger t_3 = e.multiply(r_3).add(s_3);
        BigInteger delta = cc_0.add(cc_1).add(cc_2).add(cc_3).add(cc);
        BigInteger tmp = x_1.multiply(r_1).add(x_2.multiply(r_2)).add(x_3.multiply(r_3));
        BigInteger tmp2 = BigInteger.valueOf(4).multiply(x_0).multiply(r);
        BigInteger tau = sigma.add(e.multiply(tmp.subtract(tmp2))); // error in paper,should be tau = sigma+ex_1r_1+ex_2r_2+ex_3r_3-4ex_0r

        return new RangeProofParameters(tau, z_0, z_1, z_2, z_3, t_0, t_1, t_2, t_3,
                delta, c, c_1, c_2, c_3);
    }

    public boolean verify(BigInteger g, BigInteger h, BigInteger N,
                               BigInteger a, BigInteger b,
                               RangeProofParameters proof, BigInteger e)
    {
        BigInteger c = proof.getC();
        BigInteger c_a = c.multiply(g.modPow(a.negate(),N)).pow(4).mod(N);
        BigInteger c_1 = proof.getC_1();
        BigInteger c_2 = proof.getC_2();
        BigInteger c_3 = proof.getC_3();

        BigInteger c_0 = c.modPow(BigInteger.ONE.negate(),N).multiply(g.modPow(b,N)).mod(N);

        BigInteger z_0 = proof.getZ_0();
        BigInteger z_1 = proof.getZ_1();
        BigInteger z_2 = proof.getZ_2();
        BigInteger z_3 = proof.getZ_3();

        BigInteger t_0 = proof.getT_0();
        BigInteger t_1 = proof.getT_1();
        BigInteger t_2 = proof.getT_2();
        BigInteger t_3 = proof.getT_3();

        BigInteger delta = proof.getDelta();
        BigInteger tau = proof.getTau();

        BigInteger cc_0 = g.modPow(z_0,N).multiply(h.modPow(t_0,N))
                .multiply(c_0.modPow(e.negate(),N)).mod(N);
        BigInteger cc_1 = g.modPow(z_1,N).multiply(h.modPow(t_1,N))
                .multiply(c_1.modPow(e.negate(),N)).mod(N);
        BigInteger cc_2 = g.modPow(z_2,N).multiply(h.modPow(t_2,N))
                .multiply(c_2.modPow(e.negate(),N)).mod(N);
        BigInteger cc_3 = g.modPow(z_3,N).multiply(h.modPow(t_3,N))
                .multiply(c_3.modPow(e.negate(),N)).mod(N);
        BigInteger cc = h.modPow(tau,N).multiply(g.modPow(e,N))
                .multiply(c_a.modPow(z_0,N))
                .multiply(c_1.modPow(z_1.negate(),N))
                .multiply(c_2.modPow(z_2.negate(),N))
                .multiply(c_3.modPow(z_3.negate(),N))
                .mod(N);
//        System.out.println("cc_0:"+cc_0);
//        System.out.println("cc_1:"+cc_1);
//        System.out.println("cc_2:"+cc_2);
//        System.out.println("cc_3:"+cc_3);
//        System.out.println("cc:"+cc);
        BigInteger delta_new = cc_0.add(cc_1).add(cc_2).add(cc_3).add(cc);
        if(delta_new.compareTo(delta)==0)
            return true;
        return false;
    }
}
