package uwaterloo;

import java.math.BigInteger;

public class ReportProofParameters {
    BigInteger c_r, com, E_r, E_r_a, com_a, com_b;
    BigInteger[] ss_xx, com_x;
    BigInteger ss_rr, ss_vv, ss_rr_a, ss_vv_a, ss_aa, ss_bb;
    RangeProofParameters p_a,p_b,p_r;
    RangeProofParameters[] rps;

    public ReportProofParameters(BigInteger c_r,BigInteger com,BigInteger E_r,BigInteger E_r_a,
                                 BigInteger[] ss_xx, BigInteger ss_rr, BigInteger ss_vv,
                                 BigInteger ss_rr_a, BigInteger ss_vv_a, BigInteger ss_aa,
                                 BigInteger ss_bb, RangeProofParameters p_a,
                                 RangeProofParameters p_b, RangeProofParameters p_r,
                                 RangeProofParameters[] rps, BigInteger com_a, BigInteger com_b, BigInteger[] com_x)
    {
        this.c_r = c_r;
        this.com = com;
        this.E_r = E_r;
        this.E_r_a = E_r_a;
        this.ss_xx = ss_xx;
        this.ss_rr = ss_rr;
        this.ss_vv = ss_vv;
        this.ss_rr_a = ss_rr_a;
        this.ss_vv_a = ss_vv_a;
        this.ss_aa = ss_aa;
        this.ss_bb = ss_bb;
        this.p_a = p_a;
        this.p_b = p_b;
        this.p_r = p_r;
        this.rps = rps;
        this.com_a = com_a;
        this.com_b = com_b;
        this.com_x = com_x;
    }

    public BigInteger getC_r() {
        return c_r;
    }

    public BigInteger getCom() {
        return com;
    }

    public BigInteger getE_r() {
        return E_r;
    }

    public BigInteger getE_r_a() {
        return E_r_a;
    }

    public BigInteger getSs_aa() {
        return ss_aa;
    }

    public BigInteger getSs_bb() {
        return ss_bb;
    }

    public BigInteger getSs_rr() {
        return ss_rr;
    }

    public BigInteger getSs_rr_a() {
        return ss_rr_a;
    }

    public BigInteger getSs_vv_a() {
        return ss_vv_a;
    }

    public BigInteger getSs_vv() {
        return ss_vv;
    }

    public BigInteger[] getSs_xx() {
        return ss_xx;
    }

    public RangeProofParameters getP_a() {
        return p_a;
    }

    public RangeProofParameters getP_b() {
        return p_b;
    }

    public RangeProofParameters getP_r() {
        return p_r;
    }

    public RangeProofParameters[] getRps() {
        return rps;
    }

    public BigInteger getCom_a() {
        return com_a;
    }

    public BigInteger getCom_b() {
        return com_b;
    }

    public BigInteger[] getCom_x() {
        return com_x;
    }
}
