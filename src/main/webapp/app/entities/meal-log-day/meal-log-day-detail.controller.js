(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('MealLogDayDetailController', MealLogDayDetailController);

    MealLogDayDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'MealLogDay'];

    function MealLogDayDetailController($scope, $rootScope, $stateParams, previousState, entity, MealLogDay) {
        var vm = this;

        vm.mealLogDay = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('foodlogbotadmApp:mealLogDayUpdate', function(event, result) {
            vm.mealLogDay = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
