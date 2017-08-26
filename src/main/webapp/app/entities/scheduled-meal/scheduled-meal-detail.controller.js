(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('ScheduledMealDetailController', ScheduledMealDetailController);

    ScheduledMealDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ScheduledMeal', 'User'];

    function ScheduledMealDetailController($scope, $rootScope, $stateParams, previousState, entity, ScheduledMeal, User) {
        var vm = this;

        vm.scheduledMeal = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('foodlogbotadmApp:scheduledMealUpdate', function(event, result) {
            vm.scheduledMeal = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
