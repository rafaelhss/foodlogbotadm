(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('BodyLogDialogController', BodyLogDialogController);

    BodyLogDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'BodyLog', 'User'];

    function BodyLogDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, BodyLog, User) {
        var vm = this;

        vm.bodyLog = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
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
            if (vm.bodyLog.id !== null) {
                BodyLog.update(vm.bodyLog, onSaveSuccess, onSaveError);
            } else {
                BodyLog.save(vm.bodyLog, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('foodlogbotadmApp:bodyLogUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setPhoto = function ($file, bodyLog) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        bodyLog.photo = base64Data;
                        bodyLog.photoContentType = $file.type;
                    });
                });
            }
        };
        vm.datePickerOpenStatus.bodyLogDatetime = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
