var reg = /^(25[0-5]|2[0-4]\d|[0-1]?\d?\d)(\.(25[0-5]|2[0-4]\d|[0-1]?\d?\d)){3}$|^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$/;
console.log("可驗證通過 ==============================")
console.log(reg.test("0.0.0.0"));
console.log(reg.test("1.1.1.1"));
console.log(reg.test("127.1.1.1"));
console.log(reg.test("255.255.255.255"));
console.log(reg.test("2001:0db8:85a3:08d3:1319:8a2e:0370:7344"));
console.log(reg.test("0000:0000:0000:0000:0000:0000:0000:0000"));
console.log(reg.test("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"));
console.log(reg.test("ffff:ffff:ffff:ffff:ffff:ffff:ffff:fff"));
console.log(reg.test("ffff:ffff:ffff:ffff:ffff:ffff:ffff:0"));


console.log("驗證失敗==============================");
console.log(reg.test("256.255.255.255"));
console.log(reg.test("255.255.255.256"));
console.log(reg.test("260.255.255.255"));
console.log(reg.test("256.1.1.1"));
console.log(reg.test("300.1.1.1"));
console.log(reg.test("ffff:ffff:ffff:ffff:ffff:ffff:ffff:fffg"));
console.log(reg.test("gfff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"));


