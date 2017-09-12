(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('JacaDialogController', JacaDialogController);

    JacaDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Jaca', 'User'];

    function JacaDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Jaca, User) {
        var vm = this;

        vm.jaca = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
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
            if (vm.jaca.id !== null) {
                Jaca.update(vm.jaca, onSaveSuccess, onSaveError);
            } else {
                Jaca.save(vm.jaca, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('foodlogbotadmApp:jacaUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.jacaDateTime = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
