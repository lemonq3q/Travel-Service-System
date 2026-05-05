package org.example.sentinel;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.encapsulate.ResponseResult;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(0)
@Slf4j
public class SentinelBlockExceptionHandler {

    @ExceptionHandler(BlockException.class)
    public ResponseResult<Void> handle(BlockException e, HttpServletRequest request) {
        String resource = e.getRule() == null ? null : e.getRule().getResource();
        String path = request == null ? null : request.getRequestURI();
        String location = resource != null && !resource.isBlank() ? resource : path;

        if (e instanceof FlowException) {
            log.warn("Sentinel限流：{}", location);
            return new ResponseResult<>(429, "请求被限流" + (location == null ? "" : ("：" + location)));
        }
        if (e instanceof ParamFlowException) {
            log.warn("Sentinel热点限流：{}", location);
            return new ResponseResult<>(429, "请求被热点参数限流" + (location == null ? "" : ("：" + location)));
        }
        if (e instanceof DegradeException) {
            log.warn("Sentinel熔断降级：{}", location);
            return new ResponseResult<>(503, "请求被熔断降级" + (location == null ? "" : ("：" + location)));
        }
        if (e instanceof SystemBlockException) {
            log.warn("Sentinel系统保护：{}", location);
            return new ResponseResult<>(503, "请求被系统保护" + (location == null ? "" : ("：" + location)));
        }
        if (e instanceof AuthorityException) {
            log.warn("Sentinel授权拦截：{}", location);
            return new ResponseResult<>(403, "请求被授权规则拦截" + (location == null ? "" : ("：" + location)));
        }
        log.warn("Sentinel拦截：{}，type={}", location, e.getClass().getSimpleName());
        return new ResponseResult<>(503, "请求被Sentinel拦截" + (location == null ? "" : ("：" + location)));
    }
}

