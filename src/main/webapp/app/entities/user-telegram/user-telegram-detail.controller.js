(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('UserTelegramDetailController', UserTelegramDetailController);

    UserTelegramDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'UserTelegram', 'User'];

    function UserTelegramDetailController($scope, $rootScope, $stateParams, previousState, entity, UserTelegram, User) {
        var vm = this;

        vm.userTelegram = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('foodlogbotadmApp:userTelegramUpdate', function(event, result) {
            vm.userTelegram = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
