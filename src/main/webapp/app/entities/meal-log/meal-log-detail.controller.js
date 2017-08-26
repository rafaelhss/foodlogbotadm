(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('MealLogDetailController', MealLogDetailController);

    MealLogDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'MealLog', 'ScheduledMeal', 'User'];

    function MealLogDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, MealLog, ScheduledMeal, User) {
        var vm = this;

        vm.mealLog = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('foodlogbotadmApp:mealLogUpdate', function(event, result) {
            vm.mealLog = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
