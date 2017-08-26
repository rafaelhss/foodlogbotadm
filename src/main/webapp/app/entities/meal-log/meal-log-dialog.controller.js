(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('MealLogDialogController', MealLogDialogController);

    MealLogDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'MealLog', 'ScheduledMeal', 'User'];

    function MealLogDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, MealLog, ScheduledMeal, User) {
        var vm = this;

        vm.mealLog = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.scheduledmeals = ScheduledMeal.query();
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.mealLog.id !== null) {
                MealLog.update(vm.mealLog, onSaveSuccess, onSaveError);
            } else {
                MealLog.save(vm.mealLog, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('foodlogbotadmApp:mealLogUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setPhoto = function ($file, mealLog) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        mealLog.photo = base64Data;
                        mealLog.photoContentType = $file.type;
                    });
                });
            }
        };
        vm.datePickerOpenStatus.mealDateTime = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
