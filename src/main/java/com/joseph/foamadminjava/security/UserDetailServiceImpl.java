package com.joseph.foamadminjava.security;

import com.joseph.foamadminjava.entity.SysUser;
import com.joseph.foamadminjava.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 对SpringSecurity的UserDetailsService接口的实现
 * @author Joseph.Liu
 */
@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final ISysUserService sysUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserService.getByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("用户名或密码不正确");
        }
        return new UserDetail(user.getId(),user.getUsername(),user.getPassword(),getUserAuthorities(user.getId()));
    }

    /**
     * 根据用户id,获取用户对应的权限信息，包括角色，菜单权限
     * @param userId 用户id
     * @return 授予的用户权限
     */
    public List<GrantedAuthority> getUserAuthorities(Long userId){
        //角色(ROLE_admin)、菜单操作权限 sys:user:list
        String authority = sysUserService.getUserAuthorityByUserId(userId);
        return AuthorityUtils.commaSeparatedStringToAuthorityList(authority);
    }
}
