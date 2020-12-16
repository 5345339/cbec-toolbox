package com.cbec.logistics.vendor;

import com.cbec.logistics.model.Address;
import com.cbec.logistics.model.Price;

import java.util.List;

/**
 * 物流提供商
 */
public interface LogisticsProvider {
    String getVendor();

    List<Price> getPrice(Address src, Address dst, String types, String goodAttr, float weight);
}
