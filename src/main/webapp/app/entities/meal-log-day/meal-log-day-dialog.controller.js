(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('MealLogDayDialogController', MealLogDayDialogController);

    MealLogDayDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'MealLogDay'];

    function MealLogDayDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, MealLogDay) {
        var vm = this;

        vm.mealLogDay = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.mealLogDay.id !== null) {
                MealLogDay.update(vm.mealLogDay, onSaveSuccess, onSaveError);
            } else {
                MealLogDay.save(vm.mealLogDay, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('foodlogbotadmApp:mealLogDayUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.mealLogDayDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
