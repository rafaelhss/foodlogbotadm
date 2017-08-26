(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('UserTelegramDeleteController',UserTelegramDeleteController);

    UserTelegramDeleteController.$inject = ['$uibModalInstance', 'entity', 'UserTelegram'];

    function UserTelegramDeleteController($uibModalInstance, entity, UserTelegram) {
        var vm = this;

        vm.userTelegram = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            UserTelegram.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
