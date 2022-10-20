package tk.tommy.model.to;

public class ChannelTO {
    public Long bankId;
    public String bankName;
    public DepositPaymentTerm paymentTerm;
    public DepositProvider provider;

    public ChannelTO(final Long bankId, final String bankName, final DepositPaymentTerm paymentTerm, final DepositProvider provider) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.paymentTerm = paymentTerm;
        this.provider = provider;
    }

    @Override
    public String toString() {
        return "ChannelTO{" +
            "bankId=" + bankId +
            ", bankName='" + bankName + '\'' +
            ", paymentTerm=" + paymentTerm +
            ", provider=" + provider +
            '}';
    }
}
