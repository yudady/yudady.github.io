// 整數7位小数点2位
var reg = /^([0-9]{1,7})(\.\d{1,2})?$/;
console.log("==============================")
console.log(reg.test("0"));
console.log(reg.test("0."));
console.log(reg.test("0.1"));
console.log(reg.test("0.10"));
console.log(reg.test("0.12"));
console.log(reg.test("0.123"));
console.log(reg.test(".123"));
console.log(reg.test("1.12"));
console.log(reg.test("1234567"));
console.log(reg.test("1234567.12"));
console.log(reg.test("1234567.123"));
console.log(reg.test("12345678.12"));
console.log(reg.test("12345678.12"));
console.log(reg.test("12.12"));
