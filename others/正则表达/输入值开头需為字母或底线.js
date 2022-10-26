
// 后台账号
// 输入框，栏位仅可输入半型大小写英数字及符号下底线，
// 叙述以外的字元皆且无法于兰内显示，且最多输入16个字元。



// 输入值开头需為字母或底线,大小写英数字及符号下底线
let reg = /^[_a-zA-Z][0-9a-zA-Z]{5,15}$/;
console.log("==============================")
console.log(reg.test("_"));
console.log(reg.test("_12345"));
console.log(reg.test("１２３４５６７８９０"));
console.log(reg.test("_１２３４５６７８９０"));
console.log(reg.test("a１２３４５６７８９０"));
console.log(reg.test("a中２３４５６７８９０"));
console.log(reg.test("A中２３４５６７８９０"));
console.log(reg.test("A中２３４５６７８９０"));
console.log(reg.test(""));
console.log(reg.test("_123456789"));
console.log(reg.test("a123456789"));
console.log(reg.test("_"));

console.log(reg.test("0.12"));
console.log(reg.test("0.123"));
console.log(reg.test("1.12"));
console.log(reg.test("1234567"));
console.log(reg.test("1234567.12"));
console.log(reg.test("1234567.123"));
console.log(reg.test("12345678.12"));
console.log(reg.test("12345678.12"));
console.log(reg.test("12.12"));
