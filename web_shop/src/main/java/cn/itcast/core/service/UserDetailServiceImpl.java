package cn.itcast.core.service;

import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现springSecurity的UserDetailsService接口, 完成用户的用户名, 密码校验并且
 * 如果校验通过给登录用户赋予对应的访问权限
 */
public class UserDetailServiceImpl implements UserDetailsService{

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    /**
     *
     * @param username 用户在登录页面输入的用户名
     * @return
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //定义权限集合
        List<GrantedAuthority> autoList = new ArrayList<GrantedAuthority>();
        autoList.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        if (username == null) {
            return null;
        }
        //1. 根据用户输入的用户名, 到数据库中获取对应的数据
        Seller seller = sellerService.findOne(username);

        //2. 如果获取的数据为空则证明用户名输入错误, 如果能获取到数据, 将用户名, 密码返回并且给这个用户赋予对应的访问权限
        if (seller != null) {
            //判断商家审核通过
            if ("1".equals(seller.getStatus())) {
                return new User(username, seller.getPassword(), autoList);
            }
        }
        return null;
    }
}
