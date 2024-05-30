🍞 BBangBBang_Backend_SpringBoot🥐
=============
> 2024 HSU Capstone AI를 활용한 키오스크와 스마트 빵 자판기 - BBangBBang의 SpringBoot 입니다.

<br>

## ✔️ GUIDES

AWS의 ec2 환경에서 실행하였습니다.

## ✏️ API 엔드포인트

#### 👩 사용자

http://52.79.172.135:8080/customer (GET)

http://52.79.172.135:8080/customer/signup (POST)

http://52.79.172.135:8080/customer/signIn (POST)

http://52.79.172.135:8080/customer/products (GET)

http://52.79.172.135:8080/customer/{customerId}/checkout (POST)

http://52.79.172.135:8080/customer/{customerId}/purchase (POST)

http://52.79.172.135:8080/customer/{customerId}/cart (POST,GET)

http://52.79.172.135:8080/customer/{customerId}/cart/purchase (POST)

http://52.79.172.135:8080/customer/{customerId}/cart/items/{cartItemId} (DELETE, PATCH)

http://52.79.172.135:8080/customer/{customerId}/orders (GET)

http://52.79.172.135:8080/customer/{customerId}/favorite (POST, DELETE, GET)
</br>

#### 👨‍🍳 판매자

http://52.79.172.135:8080/seller/bread (GET)

http://52.79.172.135:8080/seller/sales (GET)

http://52.79.172.135:8080/seller/orders (GET)

</br>

#### 🚪 자판기 잠금 제어

http://52.79.172.135:8080/lock (POST)

http://52.79.172.135:8080/unlock (POST)

</br>

#### 📱 키오스크

http://52.79.172.135/kiosk/bread (GET,POST)

http://52.79.172.135/kiosk/bread/{productId} (GET,PUT,DELETE)

http://52.79.172.135/kiosk/bread/order (POST)

http://52.79.172.135/kiosk/bread/order/{orderId} (GET)

http://52.79.172.135/kiosk/pick/{quickPassword} (GET)

http://52.79.172.135/kiosk/pick/bread/{orderId} (POST)

</br>

#### 🔊 음성인식

http://52.79.172.135/voice (GET, POST)


<br>

## 🕗 VERSION

- SpringBoot 3.3.0

</br>

## 📂 Database

<img width="1059" alt="스크린샷 2024-05-30 오후 8 34 30" src="https://github.com/BBangBBang-Project/BBangBBang_Backend/assets/127702076/39c30fcc-fbf6-48d3-a2da-c6e22fcf3e66">
