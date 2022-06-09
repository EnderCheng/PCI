package ca.uwaterloo.cheng;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public class RecordProofParameters {

    BigInteger ss_a, ss_b, ZZ_r, ZGa_r, D_r, E_r, U_tmp_r;
    RangeProofParameters p_a, p_b;

    public RecordProofParameters(@JsonProperty("ss_a") BigInteger ss_a,
                                 @JsonProperty("ss_b") BigInteger ss_b,
                                 @JsonProperty("ZZ_r") BigInteger ZZ_r,
                                 @JsonProperty("ZGa_r") BigInteger ZGa_r,
                                 @JsonProperty("D_r") BigInteger D_r,
                                 @JsonProperty("E_r") BigInteger E_r,
                                 @JsonProperty("U_tmp_r") BigInteger U_tmp_r,
                                 @JsonProperty("p_a") RangeProofParameters p_a,
                                 @JsonProperty("p_b") RangeProofParameters p_b)
    {
        this.ss_a=ss_a;
        this.ss_b=ss_b;
        this.ZZ_r=ZZ_r;
        this.ZGa_r=ZGa_r;
        this.D_r=D_r;
        this.E_r=E_r;
        this.U_tmp_r=U_tmp_r;
        this.p_a=p_a;
        this.p_b=p_b;
    }

    public RangeProofParameters getP_a() {
        return p_a;
    }

    public RangeProofParameters getP_b() {
        return p_b;
    }

    public BigInteger getSs_a() {
        return ss_a;
    }

    public BigInteger getSs_b() {
        return ss_b;
    }

    public BigInteger getZZ_r() {
        return ZZ_r;
    }

    public BigInteger getZGa_r() {
        return ZGa_r;
    }

    public BigInteger getD_r() {
        return D_r;
    }

    public BigInteger getE_r() {
        return E_r;
    }

    public BigInteger getU_tmp_r() {
        return U_tmp_r;
    }
}
