let arr = [1, 5, 7, 3, 4];
arr = arr.sort(function(a, b) {
  //  -1  从小到大   1 正数  从大到小
  return b - a;
});
console.table(arr);

let cart = [
    { name: "iphone", price: 12000 },
    { name: "imac", price: 18000 },
    { name: "ipad", price: 3200 }
];
cart = cart.sort(function(a, b) {
    return b.price - a.price;
});
console.table(cart);