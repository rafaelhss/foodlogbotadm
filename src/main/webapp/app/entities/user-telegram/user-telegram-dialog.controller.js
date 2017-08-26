(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('UserTelegramDialogController', UserTelegramDialogController);

    UserTelegramDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'UserTelegram', 'User'];

    function UserTelegramDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, UserTelegram, User) {
        var vm = this;

        vm.userTelegram = entity;
        vm.clear = clear;
        vm.save = save;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.userTelegram.id !== null) {
                UserTelegram.update(vm.userTelegram, onSaveSuccess, onSaveError);
            } else {
                UserTelegram.save(vm.userTelegram, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('foodlogbotadmApp:userTelegramUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
