
var app = angular.module('MyApp', []);

app.controller('MyController', ['$scope', '$http', function ($scope, $http) {
    $scope.data = {};
    $scope.viewModel = {};
    $scope.selectedCar = {};

    $http.get('test.json')
        .success(function (data) {
            $scope.data = data;
            $scope.viewModel = data.cars;
            $scope.selectedCar = $scope.viewModel[0].n;
        });

}]);

app.directive("dropdown", function() {
    return {
        restrict: 'A',
        link: function(scope) {
            scope.showDropdown = function() {
                if (scope.showList) {
                    scope.showList = false;
                    scope.overlay = false;
                }
                else {
                    scope.showList = true;
                    scope.overlay = true;
                }
            }
        }
    }
});

app.directive('dropdownOptions', function(){
    return {
        restrict: 'A',
        transclude: true,
        link: function(scope, element, attrs) {
            scope.selectItem = function(car) {
                scope.selectedCar = car;
                scope.showList = false;
                scope.overlay = false;
            }
        }
    }
});