(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('WeightDetailController', WeightDetailController);

    WeightDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Weight', 'User'];

    function WeightDetailController($scope, $rootScope, $stateParams, previousState, entity, Weight, User) {
        var vm = this;

        vm.weight = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('foodlogbotadmApp:weightUpdate', function(event, result) {
            vm.weight = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
