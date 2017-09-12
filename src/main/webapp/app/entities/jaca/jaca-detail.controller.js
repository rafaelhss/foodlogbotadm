(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('JacaDetailController', JacaDetailController);

    JacaDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Jaca', 'User'];

    function JacaDetailController($scope, $rootScope, $stateParams, previousState, entity, Jaca, User) {
        var vm = this;

        vm.jaca = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('foodlogbotadmApp:jacaUpdate', function(event, result) {
            vm.jaca = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
