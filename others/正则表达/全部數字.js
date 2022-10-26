// 整數7位小数点2位
var reg = /^\d{1,30}$/;
console.log("==============================")
console.log(reg.test(""));
console.log(reg.test("1"));
console.log(reg.test("123"));
console.log(reg.test("123456789012345"));
console.log(reg.test("1234567890123456"));
console.log(reg.test("12345678901234567"));
console.log(reg.test("123456789012345678"));
console.log(reg.test("1234567890123456789"));
console.log(reg.test("1234567d8901234567d8901234567d890"));
console.log(reg.test("1234567d8901234567d8901234567d8901"));

