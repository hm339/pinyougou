package cn.itcast.core.service;

import cn.itcast.core.pojo.address.Address;

import java.util.List;

public interface AddressService {

    public List<Address> findAddressListByUserName(String userName);
}
