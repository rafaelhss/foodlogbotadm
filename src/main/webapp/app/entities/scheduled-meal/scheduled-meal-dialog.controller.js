(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('ScheduledMealDialogController', ScheduledMealDialogController);

    ScheduledMealDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ScheduledMeal', 'User'];

    function ScheduledMealDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ScheduledMeal, User) {
        var vm = this;

        vm.scheduledMeal = entity;
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
            if (vm.scheduledMeal.id !== null) {
                ScheduledMeal.update(vm.scheduledMeal, onSaveSuccess, onSaveError);
            } else {
                ScheduledMeal.save(vm.scheduledMeal, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('foodlogbotadmApp:scheduledMealUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
