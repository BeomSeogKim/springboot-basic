package com.programmers.voucher.domain.voucher;

public interface Voucher {
    String getVoucherId();

    long discount(long originalPrice);

    void update(long updateAmount);

    long getDiscount();

    String getType();
}
