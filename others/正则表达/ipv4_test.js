var reg = /^((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$/;
console.log("==============================")
console.log(reg.test("0.0.0.0"));
console.log(reg.test("1.1.1.1"));
console.log(reg.test("127.1.1.1"));
console.log(reg.test("255.255.255.255"));
console.log(reg.test("260.255.255.255"));
console.log(reg.test("256.1.1.1"));
console.log(reg.test("300.1.1.1"));


// 2001:0db8:85a3:08d3:1319:8a2e:0370:7344
// 2001:0db8:85a3::1319:8a2e:0370:7344