import com.fasterxml.jackson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.math.BigInteger;

@DataType()
public final class ReportMessage {

    @Property()
    private final BigInteger E_x_a;

    @Property()
    private final BigInteger E_x;

    @Property()
    private final BigInteger c;

    @Property()
    private final byte[] audit_data;

    @Property()
    private final ReportProofParameters rpp;

    public BigInteger getC() {
        return c;
    }

    public BigInteger getE_x() {
        return E_x;
    }

    public BigInteger getE_x_a() {
        return E_x_a;
    }

    public ReportProofParameters getRpp() {
        return rpp;
    }

    public byte[] getAudit_data() {
        return audit_data;
    }

    public ReportMessage(@JsonProperty("E_x_a") final BigInteger E_x_a,
                         @JsonProperty("E_x") final BigInteger E_x,
                         @JsonProperty("c") final BigInteger c,
                         @JsonProperty("rpp") final ReportProofParameters rpp,
                         @JsonProperty("audit_data") final byte[] audit_data) {
        this.E_x = E_x;
        this.E_x_a = E_x_a;
        this.c = c;
        this.rpp = rpp;
        this.audit_data = audit_data;
    }

}
