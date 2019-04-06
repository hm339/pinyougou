package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.user.User;

public interface UserService {

    public void sendCode(String phone);

    public Boolean checkSmsCode(String phone , String smsCode);

    public  void  add(User user);
}
