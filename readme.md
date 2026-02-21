# 状态机 Spring Boot 3 Starter

一个基于 Spring Boot 3 的轻量级状态机框架，提供简洁易用的状态管理解决方案。

## 功能特性

- **注解驱动**：通过注解定义状态机的守卫和动作
- **灵活配置**：支持多种状态机配置方式
- **事务支持**：内置事务管理能力
- **可扩展**：提供丰富的扩展点
- **轻量级**：核心依赖少，集成简单

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>cn.jasonone.state-machine</groupId>
    <artifactId>state-machine-spring-boot3-stater</artifactId>
    <version>1.0.0</version>
</dependency>
```

或者单独添加模块依赖：

```xml
<dependency>
    <groupId>cn.jasonone.state-machine</groupId>
    <artifactId>state-machine-api</artifactId>
    <version>1.0.0</version>
</dependency>
<dependency>
    <groupId>cn.jasonone.state-machine</groupId>
    <artifactId>state-machine-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 定义状态和事件

```java
import cn.jasonone.sm.support.State;
import cn.jasonone.sm.support.Event;

// 状态枚举
public enum ApprovalStatus implements State {
    PENDING,   // 待审批
    APPROVED,  // 已通过
    REJECTED,  // 已拒绝
    CANCELED   // 已取消
    
    @Override
    public String getState() {
        return name();
    }
}

// 事件枚举
public enum ApprovalEvent implements Event {
    APPROVE,   // 审批通过
    REJECT,    // 审批拒绝
    CANCEL     // 取消
    
    @Override
    public String getCode() {
        return name();
    }
}
```

### 3. 配置状态机

```java
import cn.jasonone.sm.annotations.StateMachineAdvice;
import cn.jasonone.sm.annotations.StateMachineGuard;
import cn.jasonone.sm.annotations.StateMachineAction;
import cn.jasonone.sm.support.StateMachineEngine;
import cn.jasonone.sm.domain.StateContext;

@StateMachineAdvice
public class TestHandler {

    @StateMachineGuard(machineCode = "test", sourceState = "PENDING", targetState = "APPROVED")
    public boolean guardTestApprove(StateMachineEngine engine, StateContext ctx) {
        // 守卫逻辑
        return true;
    }

    @StateMachineAction(machineCode = "test")
    public void actionTest(StateMachineEngine engine, StateContext ctx) {
        // 动作逻辑
    }
}
```

### 4. 使用状态机

```java
import org.springframework.beans.factory.annotation.Autowired;
import cn.jasonone.sm.support.StateRegistry;
import cn.jasonone.sm.support.StateMachineEngine;
import cn.jasonone.sm.domain.Transition;

@Autowired
private StateRegistry registry;

@Autowired
private StateMachineEngine engine;

// 注册初始状态
registry.setInitialState("test", ApprovalStatus.PENDING);

// 注册状态转换
registry.registerTransition("test", new Transition(ApprovalStatus.PENDING, ApprovalEvent.APPROVE, ApprovalStatus.APPROVED));
registry.registerTransition("test", new Transition(ApprovalStatus.PENDING, ApprovalEvent.REJECT, ApprovalStatus.REJECTED));
registry.registerTransition("test", new Transition(ApprovalStatus.APPROVED, ApprovalEvent.CANCEL, ApprovalStatus.CANCELED));

// 触发事件
engine.trigger("test", "businessId", ApprovalEvent.APPROVE);
```

## 核心概念

### 1. 状态（State）

状态是状态机中的一个节点，代表业务对象的一种状态。实现 `State` 接口即可定义状态。

### 2. 事件（Event）

事件是触发状态转换的原因。实现 `Event` 接口即可定义事件。

### 3. 转换（Transition）

转换是从一个状态到另一个状态的过程，由事件触发。

### 4. 守卫（Guard）

守卫用于判断状态转换是否可以执行，返回 `true` 表示可以执行，返回 `false` 表示不可以执行。

### 5. 动作（Action）

动作是在状态转换过程中执行的操作，可以在转换前、转换中或转换后执行。

### 6. 状态上下文（StateContext）

状态上下文包含状态转换的相关信息，如状态机编码、业务ID、源状态、目标状态等。

## 注解说明

### @StateMachineAdvice

标记一个类为状态机的处理器类，该类中可以定义多个守卫和动作方法。

### @StateMachineGuard

标记一个方法为状态机的守卫方法，用于判断状态转换是否可以执行。

**参数说明：**
- `machineCode`：状态机编码，可选，不指定则对所有状态机生效
- `sourceState`：源状态，可选，不指定则对所有源状态生效
- `targetState`：目标状态，可选，不指定则对所有目标状态生效

### @StateMachineAction

标记一个方法为状态机的动作方法，用于执行状态转换时的操作。

**参数说明：**
- `machineCode`：状态机编码，可选，不指定则对所有状态机生效
- `sourceState`：源状态，可选，不指定则对所有源状态生效
- `targetState`：目标状态，可选，不指定则对所有目标状态生效
- `type`：动作类型，可选，默认为 `StateMachineActionType.ALL`

## 配置说明

### 1. 自动配置

框架会自动配置状态机相关的 Bean，无需手动配置。

### 2. 自定义配置

可以通过实现 `StateMachineConfiguration` 接口来自定义状态机配置。

## 示例代码

### 完整示例

```java
import cn.jasonone.sm.support.State;
import cn.jasonone.sm.support.Event;
import cn.jasonone.sm.annotations.StateMachineAdvice;
import cn.jasonone.sm.annotations.StateMachineGuard;
import cn.jasonone.sm.annotations.StateMachineAction;
import cn.jasonone.sm.support.StateMachineEngine;
import cn.jasonone.sm.domain.StateContext;
import cn.jasonone.sm.support.StateRegistry;
import cn.jasonone.sm.domain.Transition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// 1. 定义状态和事件
public enum OrderStatus implements State {
    CREATED,     // 已创建
    PAID,        // 已支付
    SHIPPED,     // 已发货
    DELIVERED,   // 已送达
    CANCELLED    // 已取消
    
    @Override
    public String getState() {
        return name();
    }
}

public enum OrderEvent implements Event {
    PAY,         // 支付
    SHIP,        // 发货
    DELIVER,     // 送达
    CANCEL       // 取消
    
    @Override
    public String getCode() {
        return name();
    }
}

// 2. 定义状态机处理器
@StateMachineAdvice
public class OrderStateMachineHandler {

    // 支付守卫
    @StateMachineGuard(machineCode = "order", sourceState = "CREATED", targetState = "PAID")
    public boolean canPay(StateMachineEngine engine, StateContext ctx) {
        // 检查是否可以支付
        return true;
    }

    // 支付动作
    @StateMachineAction(machineCode = "order", sourceState = "CREATED", targetState = "PAID")
    public void onPay(StateMachineEngine engine, StateContext ctx) {
        // 处理支付逻辑
        System.out.println("订单已支付");
    }

    // 发货守卫
    @StateMachineGuard(machineCode = "order", sourceState = "PAID", targetState = "SHIPPED")
    public boolean canShip(StateMachineEngine engine, StateContext ctx) {
        // 检查是否可以发货
        return true;
    }

    // 发货动作
    @StateMachineAction(machineCode = "order", sourceState = "PAID", targetState = "SHIPPED")
    public void onShip(StateMachineEngine engine, StateContext ctx) {
        // 处理发货逻辑
        System.out.println("订单已发货");
    }
}

// 3. 使用状态机
@Service
public class OrderService {

    @Autowired
    private StateRegistry registry;

    @Autowired
    private StateMachineEngine engine;

    public void initStateMachine() {
        // 注册初始状态
        registry.setInitialState("order", OrderStatus.CREATED);

        // 注册状态转换
        registry.registerTransition("order", new Transition(OrderStatus.CREATED, OrderEvent.PAY, OrderStatus.PAID));
        registry.registerTransition("order", new Transition(OrderStatus.PAID, OrderEvent.SHIP, OrderStatus.SHIPPED));
        registry.registerTransition("order", new Transition(OrderStatus.SHIPPED, OrderEvent.DELIVER, OrderStatus.DELIVERED));
        registry.registerTransition("order", new Transition(OrderStatus.CREATED, OrderEvent.CANCEL, OrderStatus.CANCELLED));
    }

    public void payOrder(String orderId) throws Exception {
        // 触发支付事件
        engine.trigger("order", orderId, OrderEvent.PAY);
    }

    public void shipOrder(String orderId) throws Exception {
        // 触发发货事件
        engine.trigger("order", orderId, OrderEvent.SHIP);
    }
}
```

## 版本说明

- **Spring Boot**: 3.5.10+
- **Java**: 17+
- **Hutool**: 5.8.39+

## 安装

### Maven

```xml
<dependency>
    <groupId>cn.jasonone.state-machine</groupId>
    <artifactId>state-machine-spring-boot3-stater</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'cn.jasonone.state-machine:state-machine-spring-boot3-stater:1.0.0'
```

## 贡献

欢迎提交 Issue 和 Pull Request 来改进这个项目。

## 许可证

本项目采用 MIT 许可证。

详细信息请参阅 [MIT License](https://opensource.org/licenses/MIT)。